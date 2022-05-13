import json
import traceback
from http import HTTPStatus
from flask import Response

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
        return Response(
            response = json.dumps({
                "error": type(exception).__name__,
                "message": str(exception),
                "traceback": ''.join(traceback.TracebackException.from_exception(exception).stack.format())
            }),
            status = status,
            headers = {
                "content-type": self.__CONTENT_TYPE__
            }
        )