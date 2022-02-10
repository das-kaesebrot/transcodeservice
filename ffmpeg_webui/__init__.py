import logging
import os
from flask import Flask
from ffmpeg_webui.classes.db import DB
from ffmpeg_webui.classes.job_service import JobService
from ffmpeg_webui.classes.config import Config

app = Flask(__name__)

from ffmpeg_webui import routes_ui, rest_api

api_version = 1

# register API at /api/vX prefix
app.register_blueprint(rest_api.rest_api, url_prefix=f'/api/v{api_version}')

def bootstrap_app():

    config = Config()

    logging.basicConfig(format='[%(asctime)s] [%(levelname)s] %(message)s', level=logging.INFO)

    # TODO fix logging config
    if config.verbose:
        logging.basicConfig(format='[%(asctime)s] [%(levelname)s] %(message)s', level=logging.DEBUG)
        logging.debug("Verbose output enabled")

    # Initialize database connection
    db = DB(username=config.username, password=config.password, hostname = config.hostname if config.hostname)

bootstrap_app()