import json
import os
from ffmpeg_webui import app
from pathlib import Path

class Config:
    FILENAME = "config.json"
    ENV_PREFIX = "FFMPEG_WEBUI"
    ACCEPTED_VARS = ["verbose", "db_hostname", "db_pass", "db_user", "db_port"]
    
    def __init__(self, path: str = ""):
        
        app.logger.debug("Entered config setting")
        
        _ = None
        with open(os.path.join(path, "config.json")) as f:
            _ = json.load(f)
        
        for var in self.ACCEPTED_VARS:
            setattr(self, var, _.get(var))
            if os.getenv(f"{self.ENV_PREFIX}_{var.upper()}"):
                setattr(self, var, os.getenv(f"{self.ENV_PREFIX}_{var.upper()}"))
            
            app.logger.debug(f"Set var \"{var}\": {getattr(self, var)}")