from http import HTTPStatus
import json
from pathlib import Path
from uuid import UUID
from ffmpeg_webui import app
from flask import Blueprint, request, jsonify
from ffmpeg_webui.classes.job_service import TranscodeJobService
from ffmpeg_webui.classes.preset import Preset
from ffmpeg_webui.classes.preset_service import PresetService
from ffmpeg_webui.classes.responsehandler import ResponseHandler

# REST API routes
rest_api = Blueprint('rest_api', __name__)

ROUTE_JOBS = "jobs"
ROUTE_PRESETS = "presets"
_jobService = TranscodeJobService()
_presetService = PresetService()
_handler = ResponseHandler()

@rest_api.route('/')
def index():
    return jsonify({
        "response": "Hello world"
    })

@rest_api.route(f"{ROUTE_JOBS}", methods=['GET'])
def GetAllJobs():
    try:
        return _handler.ConstructResponse(_jobService.get_all_jobs())
    except Exception as e:
        app.logger.error(f"{e=}")
        return _handler.ConstructErrorResponse(e)

@rest_api.route(f"{ROUTE_JOBS}/<jobId>", methods=['GET', 'POST', 'PUT', 'DELETE'])
def GetJob(jobId):
    try:
        result = _jobService.get_job_by_id(jobId)
        if not result:
            return _handler.ConstructErrorResponse(Exception("Not found"), HTTPStatus.NOT_FOUND)
        return _handler.ConstructResponse(result)
    except Exception as e:
        app.logger.error(f"{e=}")
        return _handler.ConstructErrorResponse(e)

@rest_api.route(f"{ROUTE_JOBS}", methods=['POST'])
def CreateJob():
    try:
        request_data = request.get_json()
        app.logger.debug(request_data)
        result = _jobService.insert_job(in_file = request_data.get("in_file"),
                                        out_folder = request_data.get("out_folder"),
                                        preset_id = request_data.get("preset_id"))
        return _handler.ConstructResponse("well")
    except Exception as e:
        app.logger.error(f"{e=}")
        return _handler.ConstructErrorResponse(e)


@rest_api.route(f"{ROUTE_PRESETS}", methods=['GET'])
def GetAllPresets():
    try:
        result = _presetService.get_all_presets()
        return _handler.ConstructResponse(result)
    except Exception as e:
        app.logger.error(f"{e=}")
        return _handler.ConstructErrorResponse(e)
    
@rest_api.route(f"{ROUTE_PRESETS}/<id>", methods=['GET'])
def GetPreset(id):
    try:
        result = _presetService.get_preset_by_id(id)
        return _handler.ConstructResponse(result)
    except Exception as e:
        app.logger.error(f"{e=}")
        return _handler.ConstructErrorResponse(e)

@rest_api.route(f"{ROUTE_PRESETS}", methods=['POST'])
def InsertPreset():
    try:
        request_data = request.get_json()
        preset = Preset(request_data, True)
        result = _presetService.insert_preset(preset)
        return _handler.ConstructResponse(result)
    except Exception as e:
        app.logger.error(f"{e=}")
        return _handler.ConstructErrorResponse(e)