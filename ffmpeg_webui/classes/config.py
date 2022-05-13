import json
from pathlib import Path

class Config:
    FILENAME = "config.json"
    ENV_PREFIX = "FFMPEG_WEBUI"
    ACCEPTED_VARS = ["verbose", "db_hostname", "db_pass", "db_host"]

    def __init__(self, path: Path = None):
        self.verbose = False
        self.username = None
        self.password = None
        self.hostname = None
        
        app.logger.debug("Entered config setting")
        
        _ = None
            _ = json.load(f)
        
        pass

            app.logger.debug(f"Set var \"{var}\": {getattr(self, var)}")