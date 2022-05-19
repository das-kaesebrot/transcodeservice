FROM python:3.11.0a5-alpine3.14

COPY . /var/transcodeservice
ADD requirements.txt /tmp

WORKDIR /var/transcodeservice

ENV FLASK_APP=transcodeservice
RUN pip install -r /tmp/requirements.txt

CMD [ "/usr/bin/env", "python3", "-m", "flask", "run" ]