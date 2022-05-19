from http import HTTPStatus
from uuid import UUID
from ffmpeg_webui import app
from flask import Blueprint, request, jsonify
from flask_restx import Api, Resource, fields
from ffmpeg_webui.classes.job_service import TranscodeJobService
from ffmpeg_webui.classes.preset import Preset
from ffmpeg_webui.classes.preset_service import PresetService
from ffmpeg_webui.classes.responsehandler import ResponseHandler

# REST API routes
rest_api = Blueprint('rest_api', __name__)
api = Api(rest_api, doc='/docs/', title="FFMPEG TranscodeServer REST API", default="", ordered=True)

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

@api.route('/', doc={
        "description": "Hello world route"
    },)
class Index(Resource):
    def get(self):
        return jsonify({
            "response": "Hello world"
        })


@api.route(f"{ROUTE_JOBS}/<jobId>")
@api.doc(params={'jobId': 'The specified job\'s UUID'})
class SingleJob(Resource):
    def get(self, jobId):
        try:
            result = _jobService.get_job_by_id(jobId)
            if not result:
                raise FileNotFoundError({"message": f"Object with jobId: {jobId} was not found", "status": HTTPStatus.NOT_FOUND})
            return _handler.ConstructResponse(result)
        except Exception as e:
            app.logger.error(f"{e=}")
            return _handler.ConstructErrorResponse(e)
    
    def put(self, jobId):
        pass
    
    def delete(self, jobId):
        try:
            result = _jobService.delete_job(jobId)
            return _handler.ConstructResponse(result)
        except Exception as e:
            app.logger.error(f"{e=}")
            return _handler.ConstructErrorResponse(e)

@api.route(f"{ROUTE_JOBS}")
class MultiJob(Resource):
    def get(self):
        try:
            return _handler.ConstructResponse(_jobService.get_all_jobs())
        except Exception as e:
            app.logger.error(f"{e=}")
            return _handler.ConstructErrorResponse(e)
    
    @api.expect(createJobRequestBodyFields)
    def post(self, in_file: str, out_folder: str, preset_id: UUID):
        try:
            result = _jobService.insert_job(in_file = in_file,
                                            out_folder = out_folder,
                                            preset_id = preset_id)
            return _handler.ConstructResponse("well")
        except Exception as e:
            app.logger.error(f"{e=}")
            return _handler.ConstructErrorResponse(e)
    
@api.route(f"{ROUTE_PRESETS}/<presetId>")
class SinglePreset(Resource):
    def get(self, presetId):
        try:
            result = _presetService.get_preset_by_id(presetId)
            if not result:
                return _handler.ConstructErrorResponse(Exception("Not found"), HTTPStatus.NOT_FOUND)
            return _handler.ConstructResponse(result)
        except Exception as e:
            app.logger.error(f"{e=}")
            return _handler.ConstructErrorResponse(e)
    def put(self, presetId):
        try:
            request_data = request.get_json()
            preset = Preset(request_data, False)
            preset._id = presetId
            result = _presetService.insert_preset(preset)
            return _handler.ConstructResponse(result)
        except Exception as e:
            app.logger.error(f"{e=}")
            return _handler.ConstructErrorResponse(e)

@api.route(f"{ROUTE_PRESETS}")
class MultiPreset(Resource):
    def get(self):
        try:
            result = _presetService.get_all_presets()
            return _handler.ConstructResponse(result)
        except Exception as e:
            app.logger.error(f"{e=}")
            return _handler.ConstructErrorResponse(e)
        
    def post(self):
        try:
            request_data = request.get_json()
            preset = Preset(request_data, True)
            result = _presetService.insert_preset(preset)
            return _handler.ConstructResponse(result)
        except Exception as e:
            app.logger.error(f"{e=}")
            return _handler.ConstructErrorResponse(e)