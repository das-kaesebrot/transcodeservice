from http import HTTPStatus
from uuid import UUID
from transcodeservice import app
from flask import Blueprint, request, jsonify
from flask_restx import Api, Namespace, Resource, fields
import transcodeservice
from transcodeservice.classes.job_service import TranscodeJobService
from transcodeservice.classes.preset import Preset
from transcodeservice.classes.preset_service import PresetService
from transcodeservice.classes.responsehandler import ResponseHandler
from werkzeug.exceptions import HTTPException, NotFound, BadRequest


API_VERSION = 1

# REST API routes
api = Api(app, prefix=f'/api/v{API_VERSION}', doc=f'/docs/', title="FFMPEG TranscodeServer REST API", ordered=True, version=API_VERSION)
ns = Namespace("TranscodeService", path="/transcodeservice")
api.add_namespace(ns)

ROUTE_JOBS = "/jobs"
ROUTE_PRESETS = "/presets"
_jobService = TranscodeJobService()
_presetService = PresetService()
_handler = ResponseHandler()

createJobRequestBodyFields = api.model('CreateJobRequestBody', {
    'in_file': fields.String,
    'out_folder': fields.String,
    'preset_id': fields.String
})

@ns.route('/ping', doc={
        "description": "Ping route"
    },)
class Index(Resource):
    def get(self):
        return { "data": "pong" }, 200


@ns.route(f"{ROUTE_JOBS}/<jobId>")
@ns.doc(params={'jobId': 'The specified job\'s UUID'})
class SingleJob(Resource):
    def get(self, jobId):
        result = _jobService.get_job_by_id(jobId)
        if not result:
            raise NotFound(f"Object with jobId: {jobId} was not found")
        return _handler.ConstructResponse(result)
    
    def put(self, jobId):
        pass
    
    def delete(self, jobId):
        result = _jobService.delete_job(jobId)
        return _handler.ConstructResponse(result)

@ns.route(f"{ROUTE_JOBS}")
class MultiJob(Resource):
    def get(self):
        return _handler.ConstructResponse(_jobService.get_all_jobs())
        
    @ns.expect(createJobRequestBodyFields)
    def post(self, in_file: str, out_folder: str, preset_id: UUID):
        result = _jobService.insert_job(in_file = in_file,
                                        out_folder = out_folder,
                                        preset_id = preset_id)
        return _handler.ConstructResponse("well")

@ns.route(f"{ROUTE_PRESETS}/<presetId>")
class SinglePreset(Resource):
    def get(self, presetId):
        result = _presetService.get_preset_by_id(presetId)
        if not result:
            raise NotFound(f"Object with presetId: {presetId} was not found")
        return _handler.ConstructResponse(result)
    
    def put(self, presetId):
        request_data = request.get_json()
        preset = Preset(request_data, False)
        preset._id = presetId
        result = _presetService.insert_preset(preset)
        return _handler.ConstructResponse(result)

@ns.route(f"{ROUTE_PRESETS}")
class MultiPreset(Resource):
    def get(self):
        result = _presetService.get_all_presets()
        return _handler.ConstructResponse(result)
        
    def post(self):
        request_data = request.get_json()
        preset = Preset(request_data, True)
        result = _presetService.insert_preset(preset)
        return _handler.ConstructResponse(result)