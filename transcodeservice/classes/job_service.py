from werkzeug.exceptions import HTTPException, NotFound, BadRequest
from pathlib import Path

from bson import ObjectId
from transcodeservice.classes.db import DB
from transcodeservice.classes.job import TranscodeJob

# TODO optimize DB by using preset ids as foreign keys
class TranscodeJobService:

    COLLECTION = "transcodejobs"

    def __init__(self):
        db = DB()
        self._collection = db.database[TranscodeJobService.COLLECTION]

    def get_job_by_id(self, id):
        return self._collection.find_one({
            "_id": ObjectId(id)
        })

    def get_all_jobs(self):
        return list(self._collection.find())
    
    def get_all_jobs_using_filter(self, filter: dict):
        return list(self._collection.find(filter))
    
    def get_running_jobs(self):
        return list(self._collection.find({
            "_status": TranscodeJob.RUNNING
        }))

    def insert_job(self, in_file: Path, out_folder: Path, preset_id):
        result = self._collection.insert_one(
            TranscodeJob(
                in_file = in_file,
                out_folder = out_folder,
                preset_id = preset_id
            )
        )
        
        return self.get_job_by_id(result.inserted_id)
    
    def delete_job(self, id):
        return self._collection.delete_one({
            "_id": ObjectId(id)
        })
    
    def update_job_via_put(self, job_id, in_file: Path, out_folder: Path, preset_id):
        job = TranscodeJob(
                id=job_id,
                in_file = in_file,
                out_folder = out_folder,
                preset_id = preset_id
            )
        return self.update_job(job)
    
    def update_job(self, job: TranscodeJob):
        job.update_modified()
        result = self._collection.replace_one({
            "_id": ObjectId(job._id),
        }, job)
        
        if result.matched_count == 0:
            raise NotFound(f"Object with jobId: {job._id} was not found")
        
        if result.upserted_id:
            return self.get_job_by_id(result.upserted_id)
        
        return None
            
    
    def count_failed_jobs(self):
        return self._collection.count_documents({
            "_status": TranscodeJob.FAILED
        })
    
    def count_successfully_completed_jobs(self):
        return self._collection.count_documents({
            "_status": TranscodeJob.SUCCESS
        })
    
    def count_all_jobs(self):
        return self._collection.count_documents({})

    def clear_job_history(self):
        self._collection.delete_many({
            "_status": {
                "$in" : [TranscodeJob.SUCCESS, TranscodeJob.FAILED]
            }
        })
    