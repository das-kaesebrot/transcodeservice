import ffmpeg
import os
from ffmpeg_webui import app

# Web ui routes


@app.route('/')
@app.route('/index')
def index():
    return "Hello, World!"
