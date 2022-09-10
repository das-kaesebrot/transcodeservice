from http import HTTPStatus

from bson.errors import InvalidId
from transcodeservice.rest_api import api, app
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
    
    if isinstance(e, InvalidId):
        code = int(HTTPStatus.BAD_REQUEST)
        return {
            "status": code,
            "name": type(e).__name__,
            "message": str(e)            
        }, code
        
    if app.debug:        
        response = {
                "status": HTTPStatus.INTERNAL_SERVER_ERROR,
                "name": type(e).__name__,
                "message": str(e),
                "traceback": ''.join(traceback.TracebackException.from_exception(e).stack.format())
        }
    else:
        response = default_resp
    
    return response, response.get("status")