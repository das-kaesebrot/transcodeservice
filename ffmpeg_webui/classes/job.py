# Definition file for the Job class, representing a single job object
import json
import uuid
from enum import Enum
from pathlib import Path
from preset import Preset


class TranscodeJob(Enum):
    CREATED = 1
    RUNNING = 2
    SUCCESS = 3
    FAILED = 4
    
    def __init__(self, in_file: Path, out_folder: Path, preset_id: uuid.UUID):
        self.in_files = in_file
        self.out_folder = out_folder
        self.preset_id = preset_id
        self.status = TranscodeJob.CREATED
        self.float_complete = 0.0
        self.id = self._generateUUID()

    def __repr__(self):
        return "TranscodeJob()"

    def __str__(self):
        return json.dumps(self)
    
    # TODO declare global database connection object
    def _generateUUID(self):
        while True:
            _ = uuid.uuid4()
            if _ not in db_conn.get_job_ids():
                return _

    def run(self):
        pass
    
    def abort(self):
        pass

    def getStatus(self):
        return self.status