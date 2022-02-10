# Definition file for the TranscodeJob class, representing a single job object
import json, datetime
from pathlib import Path
from uuid import UUID


class TranscodeJob(Enum):
    CREATED = 1
    RUNNING = 2
    SUCCESS = 3
    FAILED = 4
    
    def __init__(self, in_file: Path, out_folder: Path, preset_id: UUID):
        self._id = None
        self._in_files = in_file
        self._out_folder = out_folder
        self._status = TranscodeJob.CREATED
        self._float_complete: float = 0.0
        self._created_at = datetime.datetime.utcnow()
        self._preset_id = UUID

    def __repr__(self):
        return "TranscodeJob()"

    def __str__(self):
        return json.dumps(self)

    def run(self):
        pass
    
    def abort(self):
        pass

    def getStatus(self):
        return self.status