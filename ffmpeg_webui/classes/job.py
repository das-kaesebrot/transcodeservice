# Definition file for the Job class, representing a single job object
import json
from pathlib import Path
from preset import Preset


class TranscodeJob:
    
    def __init__(self, in_files: list, out_folder: Path, preset: Preset):
        self.in_files = in_files
        self.out_folder = out_folder
        self.preset = preset
        self.status = "init"
        self.float_complete = 0.0

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