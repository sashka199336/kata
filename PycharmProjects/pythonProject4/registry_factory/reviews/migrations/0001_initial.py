import reviews.models
from django.db import migrations, models
import uuid


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Review',
            fields=[
                ('id', models.UUIDField(default=uuid.uuid4, primary_key=True, serialize=False)),
                ('created_date', models.DateTimeField(auto_now_add=True)),
                ('modified_date', models.DateTimeField(auto_now=True)),
                ('project_id', models.UUIDField(default=None, null=True)),
                ('account_id', models.UUIDField(default=None, null=True)),
                ('user_id', models.UUIDField(default=None, null=True)),
                ('object_type', models.CharField(max_length=254)),
                ('object_item', models.UUIDField(db_index=True, default=None, null=True)),
                ('object_code', models.CharField(max_length=254)),
                ('name', models.CharField(max_length=254)),
                ('meta', models.JSONField(default=reviews.models.meta_default_value)),
                ('data', models.JSONField(null=True)),
            ],
        ),
    ]
