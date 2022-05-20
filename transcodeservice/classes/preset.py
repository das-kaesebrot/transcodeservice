# Definition file for the Preset class, representing a single preset object
# 
# TODO map options of ffmpeg-python to preset attributes
# https://kkroening.github.io/ffmpeg-python/

class Preset(dict):
    
    _id = None
    _description = "A default preset placeholder description"
    _vcodec = None
    _acodec = None
    _container = None
    _width = None
    _height = None
    _vbitrate = None
    _abitrate = None
    _fps = None
    
    REQUIRED_KEYS = ['v_codec', 'a_codec', 'format', 'v_bitrate', 'a_bitrate', 'a_rate']
    OPTIONAL_KEYS = ['v_rate', 'width', 'height', 'description']
    ID_KEY = ['_id']
        
    def __init__(self, data: dict, new_preset = False) -> None:
        if all(key in data for key in self.REQUIRED_KEYS):
            for key in data:
                if key not in (self.REQUIRED_KEYS + self.OPTIONAL_KEYS) or (new_preset and key == "id"):
                    raise ValueError(f"Unexpected key passed: {key}")
                setattr(self, f"{key}", data.get(key))
        else:
            raise ValueError(f"At least one key is missing",
                            {"required": self.REQUIRED_KEYS},
                            {"optional": self.OPTIONAL_KEYS})
        
    def __getattr__(self, attr):
        return self.get(attr)
    
    def Simplified(self) -> dict:
        ret_dict = {}
        for key in (self.REQUIRED_KEYS + self.OPTIONAL_KEYS + self.ID_KEY):
            if hasattr(self, key):
                ret_dict[key] = getattr(self, key)
        
        return ret_dict