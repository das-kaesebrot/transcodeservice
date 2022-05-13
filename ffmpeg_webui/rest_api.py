from http import HTTPStatus
import json
from flask import Blueprint, jsonify, Response
from ffmpeg_webui.classes.job_service import TranscodejobService

# REST API routes
rest_api = Blueprint('rest_api', __name__)
resp = Response(
    status = HTTPStatus.INTERNAL_SERVER_ERROR,
    headers = {
        "content-type": "application/json"
    }
)
resp_err = Response(
    status = HTTPStatus.INTERNAL_SERVER_ERROR,
    headers = {
        "content-type": "application/json"
    }
)

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
            resp_err.status = HTTPStatus.NOT_FOUND
            resp_err.data = json.dumps({
                "success": False,
                "error": "not found"
            })
            return resp_err
        resp.status = HTTPStatus.OK
        resp.data = json.dumps(result)
        return resp
    except Exception as e:
        resp_err.data = json.dumps({
            "error": type(e).__name__,
            "message": e.args
        })
        return resp_err