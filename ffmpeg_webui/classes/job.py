# Definition file for the TranscodeJob class, representing a single job object
import json, datetime
import ffmpeg
from pathlib import Path
from uuid import UUID


class TranscodeJob:
    CREATED = 1
    RUNNING = 2
    SUCCESS = 3
    FAILED = 4
    
    def __init__(self, id: UUID, in_file: Path, out_folder: Path, preset_id: UUID):
        
        self._id: UUID = id
        self._in_files: Path = in_file
        self._out_folder: Path = out_folder
        self._status = TranscodeJob.CREATED
        self._float_complete: float = 0.0
        self._created_at = datetime.datetime.utcnow()
        self._created_at = self._modified_at
        self._preset_id: UUID = preset_id

    def __repr__(self):
        return "TranscodeJob()"

    def __str__(self):
        return json.dumps(self)

    def run(self):
        pass
    
    def abort(self):
        pass

    def get_status(self):
        return self._status

    def get_completion_status(self):
        return self._float_complete

    def update_modified(self):
        self._modified_at = datetime.datetime.utcnow()
