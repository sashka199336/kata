from django.db.models.constants import LOOKUP_SEP

from django_filters import rest_framework as df_filters
from rest_framework import filters as rf_filters


class UUIDInFilter(df_filters.BaseInFilter, df_filters.UUIDFilter):
    pass


class LinkFilter(df_filters.FilterSet):
    id = UUIDInFilter(lookup_expr='in')
    created_date = df_filters.DateTimeFilter(field_name='created_date', lookup_expr='date__exact')
    created_date__gt = df_filters.DateTimeFilter(field_name='created_date', lookup_expr='date__gt')
    created_date__lt = df_filters.DateTimeFilter(field_name='created_date', lookup_expr='date__lt')
    modified_date = df_filters.DateTimeFilter(field_name='modified_date', lookup_expr='date__exact')
    modified_date__gt = df_filters.DateTimeFilter(field_name='modified_date', lookup_expr='date__gt')
    modified_date__lt = df_filters.DateTimeFilter(field_name='modified_date', lookup_expr='date__lt')
    object1 = UUIDInFilter(lookup_expr='in')
    object2 = UUIDInFilter(lookup_expr='in')
    weight = df_filters.NumberFilter(field_name='weight', lookup_expr='exact')
    weight__gt = df_filters.NumberFilter(field_name='weight', lookup_expr='gt')
    weight__lt = df_filters.NumberFilter(field_name='weight', lookup_expr='lt')
    direction = df_filters.NumberFilter(field_name='direction', lookup_expr='exact')
    direction__gt = df_filters.NumberFilter(field_name='direction', lookup_expr='gt')
    direction__lt = df_filters.NumberFilter(field_name='direction', lookup_expr='lt')
    meta_status = df_filters.CharFilter(method='meta_status_filter')
    meta_flags = df_filters.NumberFilter(method='meta_flags_filter')
    meta_flags__gt = df_filters.NumberFilter(method='meta_flags_filter')
    meta_flags__lt = df_filters.NumberFilter(method='meta_flags_filter')
    meta_internal_id = df_filters.CharFilter(method='meta_internal_id_filter')
    meta_internal_id__gt = df_filters.NumberFilter(method='meta_internal_id_filter')
    meta_internal_id__lt = df_filters.NumberFilter(method='meta_internal_id_filter')
    data = df_filters.CharFilter(method='data_json_filter')
    account_id = UUIDInFilter(lookup_expr='in')
    user_id = UUIDInFilter(lookup_expr='in')
    project_id = UUIDInFilter(lookup_expr='in')
    link_type = df_filters.BaseInFilter(lookup_expr='in')
    link_code = df_filters.BaseInFilter(lookup_expr='in')

    def meta_status_filter(self, queryset, name, value):
        return queryset.filter(meta__status__iexact=value)

    def meta_flags_filter(self, queryset, name, value):
        if name == 'meta_flags':
            return queryset.filter(meta__flags__exact=int(value)).order_by('meta__flags')
        elif name == 'meta_flags__gt':
            return queryset.filter(meta__flags__gt=int(value)).order_by('meta__flags')
        elif name == 'meta_flags__lt':
            return queryset.filter(meta__flags__lt=int(value)).order_by('-meta__flags')

    def meta_internal_id_filter(self, queryset, name, value):
        if name == 'meta_internal_id':
            value = value.split(',')
            return queryset.filter(meta__internal_id__in=[int(x) for x in value if x.isnumeric()]).order_by('meta__internal_id')
        elif name == 'meta_internal_id__gt':
            return queryset.filter(meta__internal_id__gt=int(value)).order_by('meta__internal_id')
        elif name == 'meta_internal_id__lt':
            return queryset.filter(meta__internal_id__lt=int(value)).order_by('-meta__internal_id')

    def data_json_filter(self, queryset, name, value):
        """
        Value format must be key__lookup=value::type or
        key__lookup=value (defaults to str) so that this method
        returns a correct queryset.
        Supported lookups: exact, iexact, gt, gte, lt, lte,
        icontains, endswith, iendswith, startswith, istartswith.
        """

        value_list = self.request.query_params.getlist('data')
        filters = []

        for value in value_list:
            try:
                column = value.split(LOOKUP_SEP)[0]
                expression = value.split(LOOKUP_SEP)[1]
                search_type = expression.split('=')[0]
                value = expression.split('::')[0].split('=')[1]
                if '::' in expression:
                    expression_type = expression.split('::')[1]
                else:
                    expression_type = 'str'
            except IndexError:
                return queryset.none()

            try:
                if expression_type == 'int':
                    value = int(value)
                elif expression_type == 'float':
                    value = float(value)
                else:
                    value = str(value)
            except ValueError:
                value = str(value)

            filter = LOOKUP_SEP.join([name, column, search_type])
            filters.append({filter: value})

        for filter_kwargs in filters:
            queryset = queryset.filter(**filter_kwargs)

        return queryset


class DynamicSearchFilter(rf_filters.SearchFilter):
    def get_search_fields(self, view, request):
        return request.GET.getlist('field',
                                   ['id', 'link_type', 'link_code', 'object1', 'object2',
                                    'weight', 'direction', 'created_date', 'modified_date',
                                    'meta', 'data', 'project_id', 'account_id', 'user_id', ])


class CustomizedOrdering(rf_filters.OrderingFilter):
    def get_valid_fields(self, queryset, view, context={}):
        # Overridden to take into account data subfields
        valid_fields = getattr(view, 'ordering_fields', self.ordering_fields)
        data_fields = []

        # Get data subfields and add them to valid fields
        ordering_params = context['request'].query_params.get('ordering')
        if ordering_params:
            ordering_params = ordering_params.split(',')
            data_fields = [(field[1:], field[1:]) if field.startswith('-') else
                           (field, field) for field in ordering_params
                           if field.startswith('data__') or field.startswith('-data__')]

        if valid_fields is None:
            # Default to allowing filtering on serializer fields
            default_valid_fields = self.get_default_valid_fields(queryset, view, context)
            default_valid_fields.extend(data_fields)

            return default_valid_fields

        elif valid_fields == '__all__':
            # View explicitly allows filtering on any model field
            valid_fields = [
                (field.name, field.verbose_name) for field in queryset.model._meta.fields
            ]
            valid_fields += [
                (key, key.title().split('__'))
                for key in queryset.query.annotations
            ]
        else:
            valid_fields = [
                (item, item) if isinstance(item, str) else item
                for item in valid_fields
            ]

        return valid_fields
