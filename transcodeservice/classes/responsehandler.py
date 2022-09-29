from email import message
from http.client import HTTPException
import json
import traceback
from http import HTTPStatus
from flask import Response
from transcodeservice import app
from transcodeservice.models.job import TranscodeJob
from transcodeservice.models.preset import Preset

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
            if isinstance(data, list):
                temp_list = []
                
                for item in data:
                    if isinstance(item, TranscodeJob) or isinstance(item, Preset):
                        temp_list.append(item.to_dict())
                        
                resp.response = json.dumps(temp_list)
                
            elif isinstance(data, TranscodeJob) or isinstance(data, Preset):
                resp.response = json.dumps(data.to_dict())

            elif isinstance(data, dict):
                resp.data = json.dumps(data)
        
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