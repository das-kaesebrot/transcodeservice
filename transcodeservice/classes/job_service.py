from werkzeug.exceptions import HTTPException, NotFound, BadRequest
from pathlib import Path

from sqlalchemy import func
from sqlalchemy.orm import Session

from transcodeservice.models.job import TranscodeJob, TranscodeJobStatus

# TODO optimize DB by using preset ids as foreign keys
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
    
    def get_running_jobs(self):
        return self._session.\
            query(TranscodeJob).\
            filter(status=TranscodeJobStatus.RUNNING).\
            all()

    def insert_job(self, in_file: Path, out_folder: Path, preset_id):
        self._session.add(TranscodeJob(
                in_file = in_file,
                out_folder = out_folder,
                preset_id = preset_id
            ))
        
        self._session.commit()
                      
        return self.get_job_by_id()
    
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
    
    def update_job(self, job: TranscodeJob):
        self._session.query(TranscodeJob).\
            filter(id=job.id).update(job)
    
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
        jobs_to_be_deleted = self._session.\
            query(TranscodeJob).\
            filter((TranscodeJob.status == TranscodeJobStatus.SUCCESS) | (TranscodeJob.status == TranscodeJobStatus.FAILED)).\
            delete()
            
        self._session.commit()