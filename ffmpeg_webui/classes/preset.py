# Definition file for the Preset class, representing a single preset object
# 
# TODO map options of ffmpeg-python to preset attributes
# https://kkroening.github.io/ffmpeg-python/

from uuid import UUID

class Preset:
    
    def __init__(self, id: UUID, description: str = None):
        self._id = id
        self._description = description