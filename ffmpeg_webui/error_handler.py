from http import HTTPStatus
from ffmpeg_webui.rest_api import api, app
from flask import Response, json, Blueprint
from werkzeug.exceptions import HTTPException
import traceback


# refer to this https://flask-restx.readthedocs.io/en/latest/errors.html

@api.errorhandler(Exception)
def handle_exception(e):
    
    default_resp = {
            "status": HTTPStatus.INTERNAL_SERVER_ERROR,
            "name": HTTPStatus.INTERNAL_SERVER_ERROR,
            "message": "Internal error",
    }
    
    # pass through HTTP errors
    if isinstance(e, HTTPException):
        # replace the body with JSON
        response = {
            "status": e.code,
            "name": e.name,
            "message": e.description,
        }
        return response, e.code
    
    response = default_resp
    
    if app.debug:
        response["traceback"] = ''.join(traceback.TracebackException.from_exception(e).stack.format())
    
    return response, response.get("status")