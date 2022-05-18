import json
import os

from attr import has
from ffmpeg_webui import app
from pathlib import Path

class Config:
    FILENAME = "config.json"
    ENV_PREFIX = "FFMPEG_WEBUI"
    ACCEPTED_VARS = ["debug", "db_hostname", "db_pass", "db_user", "db_port", "db_debug_mode"]
    
    def __init__(self, path: str = ""):
        
        app.logger.debug("Entered config setting")
        
        _ = None
        if os.path.exists(os.path.join(path, self.FILENAME)):
            with open(os.path.join(path, self.FILENAME)) as f:
                _ = json.load(f)
        
        for var in self.ACCEPTED_VARS:
            if _:
                setattr(self, var, _.get(var))
            
            if os.getenv(f"{self.ENV_PREFIX}_{var.upper()}"):
                setattr(self, var, os.getenv(f"{self.ENV_PREFIX}_{var.upper()}"))
            
            if hasattr(self, var):
                app.logger.debug(f"Set var \"{var}\": {getattr(self, var)}")