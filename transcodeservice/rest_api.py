from http import HTTPStatus
from uuid import UUID
from transcodeservice import app
from flask import Blueprint, request, jsonify
from flask_restx import Api, Namespace, Resource, fields, reqparse
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


# Look only in the querystring
jobSearchParser = reqparse.RequestParser()
jobSearchParser.add_argument('status', type=int, location='args')
jobSearchParser.add_argument

createJobRequestBodyFields = api.model('CreateJobRequestBody', {
    'in_file': fields.String,
    'out_folder': fields.String,
    'preset_id': fields.String
})

createPresetRequestBodyFields = api.model('CreatePresetRequestBody', {
    'v_codec': fields.String,
    'a_codec': fields.String,
    'format': fields.String,
    'v_bitrate': fields.String,
    'a_bitrate': fields.String,
    'a_rate': fields.String,
    'v_rate': fields.String(required=False),
    'width': fields.Integer(required=False),
    'height': fields.Integer(required=False),
    'description': fields.String(required=False)
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
    @ns.expect(jobSearchParser)
    def get(self, jobId):
        result = _jobService.get_job_by_id(jobId)
        if not result:
            raise NotFound(f"Object with {jobId=} was not found")
        return _handler.ConstructResponse(result)
    
    @ns.expect(createJobRequestBodyFields)
    @ns.response(code=int(HTTPStatus.OK), description="Update successful")
    def put(self, jobId):
        result = _jobService.update_job_via_put(
            job_id=jobId,
            in_file=api.payload["in_file"],
            out_folder=api.payload["out_folder"],
            preset_id=api.payload["preset_id"])
        return _handler.ConstructResponse(result)
    
    @ns.response(code=int(HTTPStatus.NO_CONTENT), description="On successful deletion, this method doesn't return a body.")
    def delete(self, jobId):
        result = _jobService.delete_job(jobId)
        if result.deleted_count == 0:
            raise NotFound(f"Object with {jobId=} was not found")
        return _handler.ConstructResponse(status=HTTPStatus.NO_CONTENT)

@ns.route(f"{ROUTE_JOBS}")
class MultiJob(Resource):
    def get(self):
        return _handler.ConstructResponse(_jobService.get_all_jobs())
        
    @ns.expect(createJobRequestBodyFields)
    @ns.response(code=int(HTTPStatus.CREATED), description="Creation successful")
    def post(self):
        result = _jobService.insert_job(in_file = api.payload["in_file"],
                                        out_folder = api.payload["out_folder"],
                                        preset_id = api.payload["preset_id"])
        return _handler.ConstructResponse(result, HTTPStatus.CREATED)

@ns.route(f"{ROUTE_PRESETS}/<presetId>")
class SinglePreset(Resource):
    def get(self, presetId):
        result = _presetService.get_preset_by_id(presetId)
        if not result:
            raise NotFound(f"Object with {presetId=} was not found")
        return _handler.ConstructResponse(result)
    
    def put(self, presetId):
        request_data = request.get_json()
        preset = Preset(request_data)
        preset._id = presetId
        result = _presetService.insert_preset(preset)
        return _handler.ConstructResponse(result)
    
    @ns.response(code=int(HTTPStatus.NO_CONTENT), description="On successful deletion, this method doesn't return a body.")
    def delete(self, presetId):
        result = _presetService.delete_preset(presetId)
        if result.deleted_count == 0:
            raise NotFound(f"Object with {presetId=} was not found")
        return _handler.ConstructResponse(status=HTTPStatus.NO_CONTENT)

@ns.route(f"{ROUTE_PRESETS}")
class MultiPreset(Resource):
    def get(self):
        result = _presetService.get_all_presets()
        return _handler.ConstructResponse(result)
        
    @ns.expect(createPresetRequestBodyFields)
    @ns.response(code=int(HTTPStatus.CREATED), description="Creation successful")
    def post(self):
        request_data = request.get_json()
        preset = Preset(request_data)
        result = _presetService.insert_preset(preset)
        return _handler.ConstructResponse(result, HTTPStatus.CREATED)