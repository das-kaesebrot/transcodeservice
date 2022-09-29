from http import HTTPStatus
from uuid import UUID
from transcodeservice import app
from flask import Blueprint, request, jsonify
from flask_restx import Api, Namespace, Resource, fields, reqparse
from transcodeservice.classes.job_service import TranscodeJobService
from transcodeservice.classes.db import DB
from transcodeservice.models.preset import Preset
from transcodeservice.classes.preset_service import PresetService
from transcodeservice.classes.responsehandler import ResponseHandler
from werkzeug.exceptions import HTTPException, NotFound, BadRequest
from sqlalchemy.orm import Session


API_VERSION = 1

# REST API routes
api = Api(app, prefix=f'/api/v{API_VERSION}', doc=f'/docs/', title="FFMPEG TranscodeServer REST API", ordered=True, version=API_VERSION)
ns = Namespace("TranscodeService", path="/transcodeservice")
api.add_namespace(ns)

ROUTE_JOBS = "/jobs"
ROUTE_PRESETS = "/presets"

db = DB()
_jobService = TranscodeJobService(db.get_session())
_presetService = PresetService(db.get_session())
_handler = ResponseHandler()


# Look only in the querystring
jobSearchParser = reqparse.RequestParser()
jobSearchParser.add_argument('status_i', type=int, location='args')
jobSearchParser.add_argument('status_s', type=str, location='args')
jobSearchParser.add_argument('preset_id', type=UUID, location='args')

createJobRequestBodyFields = api.model('CreateJobRequestBody', {
    'in_file': fields.String,
    'out_folder': fields.String,
    'preset_id': fields.String
})

updateJobRequestBodyFields = api.model('UpdateJobRequestBody', {
    'in_file': fields.String(required=False),
    'out_folder': fields.String(required=False),
    'preset_id': fields.String(required=False),
    'status': fields.String(enum=[x.name.lower() for x in TranscodeJobStatus], required=False)
})

createPresetRequestBodyFields = api.model('CreatePresetRequestBody', {
    'description': fields.String(required=False),
    
    'vcodec': fields.String,
    'acodec': fields.String,
    'vbitrate': fields.Integer,
    'abitrate': fields.Integer,
    'format': fields.String,
    
    'width': fields.Integer(required=False),
    'height': fields.Integer(required=False),
    'framerate': fields.Float(required=False),
    'audiorate': fields.Integer(required=False),
    'crf': fields.Integer(required=False),
    
    'videofilter': fields.String(required=False),
    'audiofilter': fields.String(required=False),
    'pix_fmt': fields.String(required=False)
})

@ns.route('/ping', doc={
        "description": "Ping route"
    },)
class Ping(Resource):
    def get(self):
        return { "data": "pong" }, 200


@ns.route(f"{ROUTE_JOBS}/<jobId>")
@ns.doc(params={'jobId': 'The specified job\'s UUID'})
class SingleJob(Resource):
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
    @ns.expect(jobSearchParser)
    def get(self):
        args = jobSearchParser.parse_args()
        status = args['status_i']
        if status:
            status = TranscodeJobStatus(status)
        else:
            status = args['status_s']
            if status:
                status = TranscodeJobStatus[status.upper()]
                
        return _handler.ConstructResponse(_jobService.get_all_jobs_with_filter(status, presetId=args['preset_id']))
        
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