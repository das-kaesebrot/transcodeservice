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
        
        if path:
            self._read_config_file(path)
        self._read_env()

    # TODO read in config file
    def _read_config_file(self, path):
        _ = {}
        with open("config.json") as f:
            _ = json.load(f)
        
        pass

    # TODO have env vars always override config file
    def _read_env(self):
        pass