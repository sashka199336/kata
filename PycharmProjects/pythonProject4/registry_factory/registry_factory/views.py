from django.http import HttpResponse
from django.conf import settings

import json


def info(request):
    data = {
        "version": settings.SPECTACULAR_SETTINGS['VERSION'],
        "add-ons": [],
    }
    json_data = json.dumps(data, indent=4)
    return HttpResponse(json_data, content_type='application/json')
