from uuid import UUID, uuid4
from ffmpeg_webui.classes.db import DB
from ffmpeg_webui.classes.job import TranscodeJob

class TranscodejobService:

    COLLECTION = "transcodejobs"

    def __init__(self, db: DB.db):
        self._collection = db[TranscodejobService.COLLECTION]
    
        
    def _generateUUID(self):
        while True:
            _ = uuid4()
            if not self._collection.find_one({
                "_id": _
            }):
                return _

    def get_job_by_id(self, job_id: UUID):
        return self._collection.find_one({
            "_id": job_id
        })


    def get_all_jobs(self):
        return list(self._collection.find())

    def insert_job(self, in_file: Path, out_folder: Path, preset_id: UUID):
        return self._collection.insert_one(
            TranscodeJob(
                id = self._generateUUID(),
                in_file = in_file,
                out_folder = out_folder,
                preset_id = preset_id
            )
        )
    
    def delete_job(self, job_id: UUID):
        return self._collection.delete_one({
            "_id": job_id
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
            "$or": [
                {
                    "_status": TranscodeJob.SUCCESS
                },
                {
                    "_status": TranscodeJob.FAILED
                }
            ]})

    