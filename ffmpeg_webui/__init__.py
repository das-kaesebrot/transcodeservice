from flask import Flask

# this needs to be created before all other imports
app = Flask(__name__)

import logging
from ffmpeg_webui import rest_api, error_handler
from ffmpeg_webui.classes.config import Config
from logging.config import dictConfig
from flask.logging import default_handler
from pymongo import results

from json import JSONEncoder
from uuid import UUID

API_VERSION = 1

# register API at /api/vX prefix
app.register_blueprint(rest_api.rest_api, url_prefix=f'/api/v{API_VERSION}')

def create_app():
    
    # configure logging
    dictConfig({
        'version': 1,
        'formatters': {'default': {
            'format': '[%(asctime)s] %(levelname)s in %(module)s: %(message)s',
        }},
        'handlers': {'wsgi': {
            'class': 'logging.StreamHandler',
            'stream': 'ext://flask.logging.wsgi_errors_stream',
            'formatter': 'default'
        }},
        'root': {
            'level': 'INFO',
            'handlers': ['wsgi']
        }
    })
    
    root = logging.getLogger()
    root.addHandler(default_handler)
    
    config = Config()
    
    if config.debug:
        app.logger.setLevel(logging.DEBUG)
        app.logger.debug("Verbose output enabled")

    # fix JSON decoder    
    old_default = JSONEncoder.default

    def new_default(self, obj):
        if isinstance(obj, UUID):
            return str(obj)
        elif isinstance(obj, results.InsertOneResult):
            return {
                "acknowledged": getattr(obj, "acknowledged"),
                "inserted_id": getattr(obj, "inserted_id")
            }
        return old_default(self, obj)

    JSONEncoder.default = new_default

    app.logger.info("Startup complete")
    app.config.ERROR_INCLUDE_MESSAGE = False

    return app

create_app()