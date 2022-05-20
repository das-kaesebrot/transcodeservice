from pathlib import Path
from transcodeservice.classes.db import DB
from transcodeservice.classes.job import TranscodeJob

class TranscodeJobService:

    COLLECTION = "transcodejobs"

    def __init__(self):
        db = DB()
        self._collection = db.database[TranscodeJobService.COLLECTION]

    def get_job_by_id(self, id):
        return self._collection.find_one({
            "_id": id
        })

    def get_all_jobs(self):
        return list(self._collection.find())
    
    def get_running_jobs(self):
        return list(self._collection.find({
            "_status": TranscodeJob.RUNNING
        }))

    def insert_job(self, in_file: Path, out_folder: Path, preset_id):
        return self._collection.insert_one(
            TranscodeJob(
                in_file = in_file,
                out_folder = out_folder,
                preset_id = preset_id
            )
        )
    
    def delete_job(self, id):
        return self._collection.delete_one({
            "_id": id
        })
    
    def update_job(self, job: TranscodeJob):
        job.update_modified()
        return self._collection.replace_one({
            "_id": job._id,
        }, job)
    
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
    