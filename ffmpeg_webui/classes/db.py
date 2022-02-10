import logging, sys
from pymongo import MongoClient
from urllib.parse import quote_plus
from pymongo.errors import ConnectionFailure

# This is the database initilization class.
# 

class DB:
    
    def __init__(
            self,
            username: str,
            password: str,
            hostname: str = "localhost",
            port: int = 27017,
            database: str = "ffmpeg_webui_db",
            tz_aware: bool = True,
            connect: bool = True
        ):

        self._client = MongoClient(
                uri = "mongodb://%s:%s@%s:%i" % (
                    
                    # In order to be able to connect using a username and password,
                    # we need to percent encode those paramter to avoid overwriting
                    # special characters ('@', '/', '+'...) reserved for the path itself

                    quote_plus(username), 
                    quote_plus(password),
                    hostname,
                    port
                ),
                tz_aware = tz_aware,
                connect = connect,
                appname = "ffmpeg_webui"
            )
        self.db = (self._client, database)

        self._try_connection()


    def _try_connection(self):
        try:
            self._client.admin.command('ping')
        except ConnectionFailure:
            logging.error("MongoDB server at %s not available" % self._client.server_info())
            sys.exit(1)
