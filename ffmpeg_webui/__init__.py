import os
import logging
from flask import Flask

# this needs to be created before all other imports
app = Flask(__name__)

from ffmpeg_webui import routes_ui, rest_api
from ffmpeg_webui.classes.db import DB
from ffmpeg_webui.classes.job_service import TranscodejobService
from ffmpeg_webui.classes.config import Config
from logging.config import dictConfig
from flask.logging import default_handler

root = logging.getLogger()
root.addHandler(default_handler)

API_VERSION = 1

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

# register API at /api/vX prefix
app.register_blueprint(rest_api.rest_api, url_prefix=f'/api/v{API_VERSION}')

def bootstrap_app():
    config = Config()
    
    # CHANGE THIS IN PROD
    debug = True
    
    if config.verbose or debug:
        app.logger.setLevel(logging.DEBUG)
        app.logger.debug("Verbose output enabled")

    # Initialize database connection
    app.logger.info("Startup complete")

bootstrap_app()