from email import message
from http.client import HTTPException
import json
import traceback
from http import HTTPStatus
from flask import Response
from ffmpeg_webui import app

class ResponseHandler:
    
    __CONTENT_TYPE__ = "application/json"
    
    def __init__(self) -> None:
        pass

    def ConstructResponse(self, data, status: HTTPStatus = HTTPStatus.OK) -> Response:
        return Response(
            response = json.dumps(data),
            status = status,
            headers = {
                "content-type": self.__CONTENT_TYPE__
            }
        )

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