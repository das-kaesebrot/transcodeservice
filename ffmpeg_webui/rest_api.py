from http import HTTPStatus
import json
from ffmpeg_webui import app
from flask import Blueprint, jsonify, Response
from ffmpeg_webui.classes.job_service import TranscodejobService

# REST API routes
rest_api = Blueprint('rest_api', __name__)

ROUTE_JOBS = "jobs"
_jobService = TranscodejobService()

@rest_api.route('/')
def index():
    return jsonify({
        "response": "Hello world"
    })

@rest_api.route(f"{ROUTE_JOBS}", methods=['GET'])
def GetAllJobs():
    return jsonify(
        _jobService.get_all_jobs()
    )

@rest_api.route(f"{ROUTE_JOBS}/<jobId>", methods=['GET', 'POST', 'PUT', 'DELETE'])
def GetJob(jobId):
    try:
        result = _jobService.get_job_by_id(jobId)
        if not result:
            return ConstructErrorResponse(Exception("Not found"), HTTPStatus.NOT_FOUND)
        return ConstructResponse(result)
    except Exception as e:
        app.logger.error(f"{e=}")
        return ConstructErrorResponse(e)


def ConstructResponse(data, status: HTTPStatus = HTTPStatus.OK) -> Response:
    return Response(
        response = json.dumps(data),
        status = status,
        headers = {
            "content-type": "application/json"
        }
    )

def ConstructErrorResponse(exception: Exception, status: HTTPStatus = HTTPStatus.INTERNAL_SERVER_ERROR) -> Response:
    return Response(
        response = json.dumps({
            "error": type(exception).__name__,
            "message": str(exception)
        }),
        status = status,
        headers = {
            "content-type": "application/json"
        }
    )