import uuid

from django.db import IntegrityError
from django.core.validators import MinValueValidator, MaxValueValidator
from django.utils import timezone

from rest_framework import serializers
from rest_framework.exceptions import ValidationError
from rest_framework.utils import html
from rest_framework.settings import api_settings
from rest_framework.validators import UniqueValidator

from .models import Link, meta_default_value, get_next_internal_id


class LinkBulkSerializer(serializers.ListSerializer):
    def to_internal_value(self, data):
        """
        This method has been overridden based on the discussion:
        https://github.com/miki725/django-rest-framework-bulk/issues/68
        to allow for sending a list of instances for validation.
        By default, it only accepts a single instance,
        which doesn't allow for bulk update.
        List of dicts of native values <- List of dicts of primitive datatypes.
        """
        if html.is_html_input(data):
            data = html.parse_html_list(data, default=[])

        if not isinstance(data, list):
            message = self.error_messages['not_a_list'].format(
                input_type=type(data).__name__
            )
            raise ValidationError({
                api_settings.NON_FIELD_ERRORS_KEY: [message]
            }, code='not_a_list')

        if not self.allow_empty and len(data) == 0:
            message = self.error_messages['empty']
            raise ValidationError({
                api_settings.NON_FIELD_ERRORS_KEY: [message]
            }, code='empty')

        if self.max_length is not None and len(data) > self.max_length:
            message = self.error_messages['max_length'].format(max_length=self.max_length)
            raise ValidationError({
                api_settings.NON_FIELD_ERRORS_KEY: [message]
            }, code='max_length')

        if self.min_length is not None and len(data) < self.min_length:
            message = self.error_messages['min_length'].format(min_length=self.min_length)
            raise ValidationError({
                api_settings.NON_FIELD_ERRORS_KEY: [message]
            }, code='min_length')

        ret = []
        errors = []

        for item in data:
            try:
                # Code that was inserted
                self.child.instance = self.instance.get(id=item['id']) if self.instance else None
                self.child.initial_data = item
                # Until here
                validated = self.child.run_validation(item)
            except ValidationError as exc:
                errors.append(exc.detail)
            else:
                ret.append(validated)
                errors.append({})

        if any(errors):
            raise ValidationError(errors)

        return ret

    def create(self, validated_data):
        """
        Create method has been overridden so that it first makes
        a list of all links to be created and then sends a
        single bulk_create call to the database.
        """
        link_data = []
        for item in validated_data:
            # Populate meta with new internal_id, get meta.status
            # and meta.flags from request if provided.
            internal_id = get_next_internal_id()  # Fetch a unique internal_id for each object
            meta_data = meta_default_value()
            meta_data["internal_id"] = internal_id
            if "meta" not in item:
                item["meta"] = meta_default_value()
            if "flags" in item["meta"]:
                meta_data["flags"] = item["meta"]["flags"]
            if "status" in item["meta"]:
                meta_data["status"] = item["meta"]["status"]
            item.pop("meta")
            link_data.append(Link(meta=meta_data, **item))

        return Link.objects.bulk_create(link_data)

    def update(self, instances, validated_data):
        """
        The update method has been overridden so that it first collects
        the updates needed for all instances and then bulk_updates
        them all at once.
        """
        instance_hash = {index: instance for index, instance in enumerate(instances)}

        result = [
            self.child.update(instance_hash[index], attrs)
            for index, attrs in enumerate(validated_data)
        ]

        writable_fields = [
            x
            for x in self.child.Meta.fields
            if x not in ['id', 'created_date']  # modified_date has to be updated
        ]

        try:
            self.child.Meta.model.objects.bulk_update(result, writable_fields)
        except IntegrityError as e:
            raise ValidationError(e)

        return result


class LinkSerializer(serializers.ModelSerializer):
    id = serializers.UUIDField(validators=[UniqueValidator(queryset=Link.objects.all())],
                               allow_null=True)
    weight = serializers.FloatField(validators=[MinValueValidator(0.0),
                                                MaxValueValidator(1.0)],
                                    default=0.0)
    direction = serializers.IntegerField(validators=[MinValueValidator(0),
                                                     MaxValueValidator(3)],
                                         default=1)

    def __init__(self, *args, **kwargs):
        """
        This method has been overridden to generate random UUIDs in "id" field
        if "id" value is not passed in request.
        """
        if "context" in kwargs and "data" in kwargs and kwargs["context"]["request"].method == "POST":
            if not isinstance(kwargs["data"], list) and "id" not in kwargs["data"]:
                kwargs["data"]["id"] = uuid.uuid4()
            if isinstance(kwargs["data"], list):
                for idx, item in enumerate(kwargs["data"]):
                    if "id" not in item:
                        kwargs["data"][idx]["id"] = uuid.uuid4()
        super().__init__(*args, **kwargs)

    def create(self, validated_data):
        # Get meta.status and meta.flags from request if provided.
        meta_data = meta_default_value()
        if "meta" not in validated_data:
            validated_data["meta"] = meta_default_value()
        if "flags" in validated_data["meta"]:
            meta_data["flags"] = validated_data["meta"]["flags"]
        if "status" in validated_data["meta"]:
            meta_data["status"] = validated_data["meta"]["status"]
        validated_data.pop("meta")
        instance = Link.objects.create(meta=meta_data, **validated_data)

        return instance

    def update(self, instance, validated_data):
        """
        Update method has been overridden so that it no longer saves,
        but returns new instances to parent serializer, if list data is
        passed to it. Parent serializer then does bulk_update on the list
        of instances.

        If a single instance is passed though, it is saved to the database.
        """
        partial = self.context['request'].method == 'PATCH'

        if partial:
            # Get JSON fields from the validated data
            meta = validated_data.pop('meta', None)
            data = validated_data.pop('data', None)

            # Perform partial update for the JSON fields
            if meta is not None:
                instance.meta.update(meta)

            if data is not None:
                instance.data.update(data)

            for attr, value in validated_data.items():
                setattr(instance, attr, value)
        else:
            meta = validated_data.pop('meta', None)  # meta fields should always be preserved

            if meta is not None:
                instance.meta.update(meta)

            for attr, value in validated_data.items():
                setattr(instance, attr, value)

        # Bulk_update in django does not trigger auto_now,
        # so modified date needs to be set explicitly
        setattr(instance, 'modified_date', timezone.now())

        if isinstance(self._kwargs['data'], dict):
            instance.save()

        return instance

    class Meta:
        model = Link
        fields = ['id', 'link_type', 'link_code', 'object1', 'object2',
                  'weight', 'direction', 'created_date', 'modified_date',
                  'meta', 'data', 'project_id', 'account_id', 'user_id', ]
        read_only_fields = ['id', 'created_date', 'modified_date',]
        list_serializer_class = LinkBulkSerializer
