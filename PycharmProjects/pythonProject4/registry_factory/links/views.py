from django.db.models import Func, F, Value, JSONField
from rest_framework import viewsets, mixins, status, serializers
from rest_framework.response import Response
from drf_spectacular.utils import extend_schema, OpenApiParameter, \
    inline_serializer, OpenApiResponse, OpenApiExample
from drf_spectacular.types import OpenApiTypes

from django_filters import rest_framework as df_filters

from .serializers import LinkSerializer
from .models import Link
from .spectacular import *
from .filters import DynamicSearchFilter, LinkFilter, CustomizedOrdering


@extend_schema(tags=['Links controllers'])
class LinksViewset(mixins.ListModelMixin,
                   mixins.CreateModelMixin,
                   viewsets.GenericViewSet):
    view_type = 'multiple_objects'  # added this attribute to work with overridden metadata.py
    queryset = Link.objects.all()
    serializer_class = LinkSerializer
    filter_backends = [df_filters.DjangoFilterBackend, DynamicSearchFilter, CustomizedOrdering]
    filterset_class = LinkFilter

    @extend_schema(
        parameters=[
            OpenApiParameter("direction", OpenApiTypes.INT, enum=[0, 1, 2, 3],
                             description="""* `0` - 00\n\n* `1` - 01\n* `2` - 10\n* `3` - 11"""),
            OpenApiParameter("ordering",
                             many=True,
                             description="Multiple values may be separated by commas.",
                             explode=False,
                             location=OpenApiParameter.QUERY),
            OpenApiParameter("data", description="*Query format:* key__lookup=value[::type]. "
                                                 "If type is omitted, str is default."
                                                 "\n\n *Example:* "
                                                 "username__exact=Ivan *or* number__gte=18::int"),
            OpenApiParameter("meta_internal_id",
                             many=True,
                             description="Multiple values may be separated by commas.",
                             explode=False,
                             location=OpenApiParameter.QUERY,
                             type=int),
        ],
        examples=[
            OpenApiExample(
                name='Links list response example',
                value=links_list_response_example,
                response_only=True,
            )
        ]
    )
    def list(self, request, *args, **kwargs):
        return super().list(request, *args, **kwargs)

    @extend_schema(
        examples=[
            OpenApiExample(
                name='Links bulk create request example',
                value=links_bulk_create_request_example,
                request_only=True,
            ),
            OpenApiExample(
                name='Links bulk create response example',
                value=links_bulk_create_response_example,
                response_only=True,
            )
        ]
    )
    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data, many=True)
        serializer.is_valid(raise_exception=True)
        self.perform_create(serializer)
        headers = self.get_success_headers(serializer.data)
        return Response(serializer.data, status=status.HTTP_201_CREATED, headers=headers)

    @extend_schema(
        examples=[
            OpenApiExample(
                name='Links bulk put request example',
                value=links_bulk_put_request_example,
                request_only=True,
            ),
            OpenApiExample(
                name='Links bulk put response example',
                value=links_bulk_put_response_example,
                response_only=True,
            )
        ]
    )
    def bulk_update(self, request, *args, **kwargs):
        links_data = request.data
        sorted_links_data = sorted(links_data, key=lambda x: x['id'])  # Sort data by id so that it didn't matter
        ids = []                                                       # in which order instances are sent

        for link in sorted_links_data:
            ids.append(link.get('id'))

        instances = Link.objects.filter(id__in=ids).order_by('id')  # We only take instances that need to be updated

        serializer = self.get_serializer(
            instances, data=sorted_links_data, partial=False, many=True
        )
        serializer.is_valid(raise_exception=True)
        self.perform_update(serializer)

        return Response(serializer.data)

    @extend_schema(
        examples=[
            OpenApiExample(
                name='Links bulk patch request example',
                value=links_bulk_patch_request_example,
                request_only=True,
            ),
            OpenApiExample(
                name='Links bulk patch response example',
                value=links_bulk_patch_response_example,
                response_only=True,
            )
        ]
    )
    def bulk_partial_update(self, request, *args, **kwargs):
        links_data = request.data
        sorted_links_data = sorted(links_data, key=lambda x: x['id'])  # Sort data by id so that it didn't matter
        ids = []                                                       # in which order instances are sent

        for link in sorted_links_data:
            ids.append(link.get('id'))

        instances = Link.objects.filter(id__in=ids).order_by('id')  # We only take instances that need to be updated

        serializer = self.get_serializer(
            instances, data=sorted_links_data, partial=True, many=True
        )
        serializer.is_valid(raise_exception=True)
        self.perform_update(serializer)

        return Response(serializer.data)

    @extend_schema(
        parameters=[
            OpenApiParameter("id",
                             type=OpenApiTypes.UUID,
                             many=True,
                             description="Multiple values may be separated by commas.",
                             explode=False,
                             location=OpenApiParameter.QUERY,
                             required=True)
        ],
        responses={
            200: OpenApiResponse(
                inline_serializer(
                    name='LinksDeactivatedResponse',
                    fields={
                        'message': serializers.CharField(default="Links deactivated"),
                    }
                ),
                description="Deactivated",
            )
        },
    )
    def bulk_deactivate(self, request, *args, **kwargs):
        id_string = request.query_params['id']
        id_list = id_string.split(',')
        objs = Link.objects.filter(id__in=id_list)
        # JSON field update implemented as described at
        # https://stackoverflow.com/questions/36680691/updating-jsonfield-in-django-rest-framework?rq=4
        objs.update(
            meta=Func(
                F("meta"),
                Value(["status"]),
                Value("inactive", JSONField()),
                function="jsonb_set",
            )
        )
        if len(id_list) == len(objs):
            return Response(data={"message": "Links deactivated"},
                            status=status.HTTP_200_OK)
        else:
            return Response(data={"message": f"{len(objs)} links deactivated out of {len(id_list)}. "
                                             f"Some ids are either duplicated or not in the database."},
                            status=status.HTTP_200_OK)

    def perform_update(self, serializer):
        serializer.save()


@extend_schema(tags=['Link controllers'])
class LinkViewset(mixins.RetrieveModelMixin,
                  mixins.CreateModelMixin,
                  mixins.UpdateModelMixin,
                  viewsets.GenericViewSet):
    view_type = 'single_object'  # added this attribute to work with overridden metadata.py
    queryset = Link.objects.all()
    serializer_class = LinkSerializer

    @extend_schema(
        examples=[
            OpenApiExample(
                name='Links single retrieve response example',
                value=links_single_retrieve_response_example,
                response_only=True,
            )
        ]
    )
    def retrieve(self, request, *args, **kwargs):
        return super().retrieve(request, *args, **kwargs)

    @ extend_schema(
        examples=[
            OpenApiExample(
                name='Links single create request example',
                value=links_single_create_request_example,
                request_only=True,
            ),
            OpenApiExample(
                name='Links single create response example',
                value=links_single_create_response_example,
                response_only=True,
            )
        ]
    )
    def create(self, request, *args, **kwargs):
        return super().create(request, *args, **kwargs)

    @extend_schema(
        examples=[
            OpenApiExample(
                name='Links single put request example',
                value=links_single_put_request_example,
                request_only=True,
            ),
            OpenApiExample(
                name='Links single put response example',
                value=links_single_put_response_example,
                response_only=True,
            )
        ]
    )
    def update(self, request, *args, **kwargs):
        return super().update(request, *args, **kwargs)

    @extend_schema(
        examples=[
            OpenApiExample(
                name='Links single patch request example',
                value=links_single_patch_request_example,
                request_only=True,
            ),
            OpenApiExample(
                name='Links single patch response example',
                value=links_single_patch_response_example,
                response_only=True,
            )
        ]
    )
    def partial_update(self, request, *args, **kwargs):
        return super().partial_update(request, *args, **kwargs)

    @extend_schema(responses={
            200: OpenApiResponse(
                inline_serializer(
                    name='LinkDeactivatedResponse',
                    fields={
                        'message': serializers.CharField(default="Link deactivated"),
                    }
                ),
                description="Deactivated",
            )
        },
    )
    def deactivate(self, request, *args, **kwargs):
        instance = self.get_object()
        meta_flags = instance.meta['flags']
        instance.meta['status'] = 'inactive'
        instance.meta['flags'] = meta_flags
        instance.save()
        return Response(data={"message": "Link deactivated"},
                        status=status.HTTP_200_OK)
