import sys
from transcodeservice.classes.config import Config
from transcodeservice.models.base import Base
from transcodeservice import app
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.engine import Engine

# This is the database initilization class.

class DB:
    
    engine: Engine
    
    def __init__(self):
                
        conf = Config()

        conn_string = getattr(conf, "db_string")
        echo = getattr(conf, "db_debug_mode")
        
        app.logger.debug(f"Trying conn with {conn_string=}")
        
        self.engine = create_engine(
                url = conn_string,
                echo = echo,
                future = True,
                pool_pre_ping = True
            )
        
        self.engine.connect()
        
        Base.metadata.create_all(bind=self.engine)
        
        app.logger.debug(f"Connected to {conn_string=}")
        
    def get_session(self) -> Session:
        return Session(self.engine, future=True)
