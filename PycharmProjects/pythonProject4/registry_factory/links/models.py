import uuid

from django.contrib.postgres.indexes import GinIndex, Index
from django.db import models, connection
from django.core.validators import MinValueValidator, MaxValueValidator
from django.db.models.signals import pre_save
from django.dispatch import receiver


def get_next_internal_id():
    with connection.cursor() as cursor:
        cursor.execute("CREATE SEQUENCE IF NOT EXISTS links_internal_id_seq START WITH 1 INCREMENT BY 1")
        cursor.execute("SELECT nextval('links_internal_id_seq')")
        result = cursor.fetchone()
        return result[0]


def meta_default_value(internal_id_placeholder=None):
    return {
        "status": "active",
        "flags": 0,
        "internal_id": internal_id_placeholder
    }


class Link(models.Model):

    DIRECTION_CHOICES = [
        (0, '00'),
        (1, '01'),
        (2, '10'),
        (3, '11'),
    ]

    id = models.UUIDField(
        primary_key=True,
        default=uuid.uuid4,
        editable=False
    )
    link_type = models.CharField(max_length=254)
    link_code = models.CharField(max_length=254, null=True)
    object1 = models.UUIDField()
    object2 = models.UUIDField()
    weight = models.FloatField(validators=[MinValueValidator(0.0),
                                           MaxValueValidator(1.0)],
                               default=0.0)
    direction = models.SmallIntegerField(choices=DIRECTION_CHOICES,
                                         validators=[MinValueValidator(0),
                                                     MaxValueValidator(3)],
                                         default=1)
    created_date = models.DateTimeField(auto_now_add=True)
    modified_date = models.DateTimeField(auto_now=True)
    meta = models.JSONField(default=meta_default_value)
    data = models.JSONField(default=dict, null=True)
    project_id = models.UUIDField(default=None, null=True)
    account_id = models.UUIDField(default=None, null=True)
    user_id = models.UUIDField(default=None, null=True)

    class Meta:
        constraints = (
            models.CheckConstraint(
                check=models.Q(weight__gte=0.0) & models.Q(weight__lte=1.0),
                name='link_weight_range'
            ),
            models.CheckConstraint(
                check=models.Q(direction__gte=0) & models.Q(direction__lte=3),
                name='link_direction_range'
            ),
            models.UniqueConstraint(
                fields=['link_code', 'project_id', 'link_type'],
                name="link_code_proj_type"
            ),
            models.UniqueConstraint(
                fields=['link_code', 'project_id', 'account_id', 'link_type'],
                name="link_code_proj_acc_type"
            )
        )
        indexes = [
            GinIndex(fields=['data'], name='links_data_gin'),
            Index(fields=['link_type'], name='links_link_type_idx'),
            Index(fields=['object1', 'link_type', 'object2'], name='links_obj1_link_type_obj2_idx'),
            Index(fields=['created_date'], name='links_created_date_idx'),
            Index(fields=['modified_date'], name='links_modified_date_idx'),
            Index(fields=['project_id', 'account_id', 'user_id'], name='links_prj_id_acc_id_usr_id_idx'),
            Index(fields=['account_id', 'user_id'], name='links_account_id_user_id_idx'),
            Index(fields=['user_id'], name='links_user_id_idx'),
            Index(fields=['object2', 'object1', 'link_type'], name='links_obj2_obj1_lt_idx'),
            Index(fields=['object1', 'object2', 'link_type'], name='links_obj1_obj2_lt_idx'),
            Index(fields=['object2', 'link_type', 'object1'], name='links_obj2_lt_obj1_idx'),
            Index(fields=['link_code', 'project_id', 'link_type'], name='link_code_proj_type_idx'),
            Index(fields=['link_code', 'project_id', 'account_id', 'link_type'], name="link_code_proj_acc_type_idx"),
        ]
        ordering = ['-meta__internal_id']


@receiver(pre_save, sender=Link)
def set_meta(sender, instance, **kwargs):
    try:
        Link.objects.get(id=instance.id)
    except Link.DoesNotExist:
        if 'status' not in instance.meta.keys():
            instance.meta['status'] = "active"
        if 'flags' not in instance.meta.keys():
            instance.meta['flags'] = 0
        instance.meta['internal_id'] = get_next_internal_id()
