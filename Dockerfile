FROM python:3.11.0a5-alpine3.14

COPY . /var/ffmpeg_webui
ADD requirements.txt /tmp

WORKDIR /var/ffmpeg_webui

ENV FLASK_APP=ffmpeg_webui
RUN pip install -r /tmp/requirements.txt

CMD [ "/usr/bin/env", "python3", "-m", "flask", "run" ]