import sys
from ffmpeg_webui.classes.config import Config
from ffmpeg_webui import app
from pymongo import MongoClient
from urllib.parse import quote_plus
from pymongo.errors import ConnectionFailure, ServerSelectionTimeoutError

# This is the database initilization class.

class DB:
    
    def __init__(self):
                
        conf = Config()
                        
        username = getattr(conf, "db_user", None)
        password = getattr(conf, "db_pass", None)
        hostname = getattr(conf, "db_hostname", "localhost")
        port = getattr(conf, "db_port", 27017)
        database = getattr(conf, "db_database", "ffmpeg_webui_db")
        tz_aware = getattr(conf, "db_tz_aware", True)
        connect = True
        
        if username:
            host = "mongodb://%s:%s@%s:%i" % (
                    
                    # In order to be able to connect using a username and password,
                    # we need to percent encode those paramter to avoid overwriting
                    # special characters ('@', '/', '+'...) reserved for the path itself

                    quote_plus(username), 
                    quote_plus(password),
                    hostname,
                    port
                )
        else: host = "mongodb://%s:%i" % (                    
                    hostname,
                    port
                )
        
        self._client = MongoClient(
                host = host,
                tz_aware = tz_aware,
                connect = connect,
                appname = "ffmpeg_webui"
            )
        
        self.database = self._client[database]

        app.logger.debug(f"Trying conn with {host}")
        self._try_connection()
        app.logger.debug(f"Connected to {host}")


    def _try_connection(self):
        try:
            self._client.admin.command('ping')
        except ConnectionFailure or ServerSelectionTimeoutError:
            app.logger.error("MongoDB server at %s not available" % self._client.server_info())
            sys.exit(1)
