from flask import Flask

app = Flask(__name__)

from ffmpeg_webui import routes_ui, rest_api

api_version = 1

# register API at /api/vX prefix
app.register_blueprint(rest_api.rest_api, url_prefix=f'/api/v{api_version}')