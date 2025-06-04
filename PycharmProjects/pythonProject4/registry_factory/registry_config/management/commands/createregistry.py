from django.core.management.templates import TemplateCommand


class Command(TemplateCommand):
    help = (
        "Creates a registry from registry_template.zip."
    )
    missing_args_message = "You must provide an application name."

    def handle(self, **options):
        app_name = options.pop("name")
        target = options.pop("directory")
        super().handle("app", app_name, target, **options)

    def add_arguments(self, parser):
        super().add_arguments(parser)
        parser.add_argument(
            "--model_name",
            help="Custom option to be used in template context.",
        )
