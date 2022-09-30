from werkzeug.exceptions import HTTPException, NotFound, BadRequest
from pathlib import Path

from sqlalchemy import func
from sqlalchemy.orm import Session

from transcodeservice.models.job import TranscodeJob, TranscodeJobStatus
from transcodeservice.models.preset import Preset

class TranscodeJobService:
    
    def __init__(self, session: Session) -> None:
        self._session = session

    def get_job_by_id(self, id):
        return self._session.\
            get(TranscodeJob, id)

    def get_all_jobs(self):
        return self._session.\
            query(TranscodeJob).\
            all()

    def get_all_jobs_with_filter(self, status: TranscodeJobStatus = None, presetId: Preset.id = None):
        q = self._session.query(TranscodeJob)
        if status:
            q = q.filter_by(status=status)
        
        if presetId:
            q = q.filter_by(preset_id=presetId)
            
        return q.all()

    def get_running_jobs(self):
        return self._session.\
            query(TranscodeJob).\
            filter_by(status=TranscodeJobStatus.RUNNING).\
            all()

    def insert_job(self, in_file: Path, out_folder: Path, preset_id) -> TranscodeJob:
        
        if not self._session.get(Preset, preset_id):
            raise BadRequest(f"Unable to find a preset by {preset_id=}")
        
        job = TranscodeJob(
                in_file = in_file,
                out_folder = out_folder,
                preset_id = preset_id
            )
        
        self._session.add(job)
        
        self._session.commit()
        self._session.refresh(job)
        return job        
    
    def delete_job(self, id):
        self._session.delete(self.get_job_by_id(id))
        self._session.commit()
    
    def update_job_via_put(self, job_id, in_file: Path, out_folder: Path, preset_id):
        job = TranscodeJob(
                id=job_id,
                in_file = in_file,
                out_folder = out_folder,
                preset_id = preset_id
            )
        return self.update_job(job)
    
    def update_job(self, job: TranscodeJob) -> TranscodeJob:
        self._session.query(TranscodeJob).\
            filter_by(id=job.id).update(job.to_dict_for_update())
        
        self._session.commit()
        self._session.refresh(job)
        return job        
    
    def count_failed_jobs(self):
        return self._session.\
            query(func.count(TranscodeJob.id)).\
            filter(status=TranscodeJobStatus.FAILED)
    
    def count_successfully_completed_jobs(self):
        return self._session.\
            query(func.count(TranscodeJob.id)).\
            filter(status=TranscodeJobStatus.SUCCESS)
    
    def count_all_jobs(self):
        return self._session.\
            query(func.count(TranscodeJob.id))

    def clear_job_history(self):
        self._session.\
            query(TranscodeJob).\
            filter_by((TranscodeJob.status == TranscodeJobStatus.SUCCESS) | (TranscodeJob.status == TranscodeJobStatus.FAILED)).\
            delete()
            
        self._session.commit()