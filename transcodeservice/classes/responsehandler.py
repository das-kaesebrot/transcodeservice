from email import message
from http.client import HTTPException
import json
import traceback
from http import HTTPStatus
from flask import Response
from transcodeservice import app
from bson import json_util

class ResponseHandler:
    
    __CONTENT_TYPE__ = "application/json"
    
    def __init__(self) -> None:
        pass

    def ConstructResponse(self, data = None, status: HTTPStatus = HTTPStatus.OK) -> Response:
        resp = Response(
            status = status,
            headers = {
                "content-type": self.__CONTENT_TYPE__
            }
        )
        if data:
            resp.response = json_util.dumps(data)
        
        return resp

    def ConstructErrorResponse(self, exception: Exception, status: HTTPStatus = HTTPStatus.INTERNAL_SERVER_ERROR) -> Response:
        app.logger.error(f"{exception}")
        message = str(exception)
        if isinstance(exception, FileNotFoundError):
            for arg in exception.args:
                if isinstance(arg, dict):
                    status = arg.get("status")
                    message = arg.get("message")

        response = {
                "error": type(exception).__name__,
                "message": message
        }
        
        if app.debug:
            response["traceback"] = ''.join(traceback.TracebackException.from_exception(exception).stack.format())
        return Response(
            response = json.dumps(response),
            status = status,
            headers = {
                "content-type": self.__CONTENT_TYPE__
            }
        )