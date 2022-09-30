import json
import os

from transcodeservice import app
from urllib.parse import quote_plus

from transcodeservice.util.str2bool import str2bool

class Config:
    FILENAME = "config.json"
    ENV_PREFIX = "TRANSCODESERVICE"
    ACCEPTED_VARS_WITH_DEFAULTS = [
        ("debug", bool, False),
        ("db_string", str, None),
        ("db_debug_mode", bool, False),
        ("db_host", str, None),
        ("db_pass", str, None),
        ("db_user", str, None),
        ("db_port", int, 0),
        ("db_database", str, "transcodeservice.db"),
        ("db_dialect", str, "sqlite")
    ]
    
    def __init__(self, path: str = ""):
        
        app.logger.debug("Entered config setting")
        
        _ = None
        if os.path.exists(os.path.join(path, self.FILENAME)):
            with open(os.path.join(path, self.FILENAME)) as f:
                _ = json.load(f)
        
        for var_tuple in self.ACCEPTED_VARS_WITH_DEFAULTS:
            var = var_tuple[0]
            expected_type = var_tuple[1]
            defaultval = var_tuple[2]
            env_var = f"{self.ENV_PREFIX}_{var.upper()}"
            
            if _.get(var):
                setattr(self, var, _.get(var))
                app.logger.debug(f"Set var \"{var}\" ({expected_type.__name__}) from {self.FILENAME}: {getattr(self, var)}")
            
            # sometimes wishing that I used a language with strict typing
            if os.getenv(env_var):
                value = os.getenv(f"{self.ENV_PREFIX}_{var.upper()}")
                if expected_type == bool:
                    value = str2bool(value)
                
                setattr(self, var, value)
                
                app.logger.debug(f"Set var \"{var}\" ({expected_type.__name__}) from env var {env_var}: {value}")
                        
            if not hasattr(self, var):
                setattr(self, var, defaultval)
                app.logger.debug(f"Set var \"{var}\" ({expected_type.__name__}) to fallback: {defaultval}")
        
        for var_tuple in self.ACCEPTED_VARS_WITH_DEFAULTS:
            var = var_tuple[0]
            expected_type = var_tuple[1]
            app.logger.debug(f"Config var \"{var}\" ({expected_type.__name__}): {getattr(self, var)}")
        
        self.construct_db_string()
            
        if not self.db_debug_mode:
            self.db_debug_mode = False
            
    def construct_db_string(self) -> None:        
        if not self.db_string:
            if self.db_user:
                self.db_string = "%s://%s:%s@%s:%i/%s" % (                    
                    # In order to be able to connect using a username and password,
                    # we need to percent encode those parameters to avoid overwriting
                    # special characters ('@', '/', '+'...) reserved for the path itself

                    self.db_dialect,
                    quote_plus(self.db_user), 
                    quote_plus(self.db_pass),
                    self.db_host,
                    self.db_port,
                    self.db_database
                )
            elif self.db_host:
                self.db_string = "%s://%s:%i/%s" % (   
                    self.db_dialect,
                    self.db_host,
                    self.db_port,
                    self.db_database
                )
            else:
                self.db_string = "%s:///%s" % (
                    self.db_dialect,
                    self.db_database
                )
        
        app.logger.debug(f"Constructed database connection string as '{self.db_string}'")
        