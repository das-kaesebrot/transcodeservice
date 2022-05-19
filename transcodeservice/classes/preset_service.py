from uuid import UUID, uuid4
from transcodeservice.classes.db import DB
from transcodeservice.classes.preset import Preset

class PresetService:

    COLLECTION = "presets"

    def __init__(self):
        db = DB()
        self._collection = db.database[PresetService.COLLECTION]
    
    def _generateUUID(self) -> UUID:
        while True:
            _ = uuid4()
            if not self._collection.find_one({
                "_id": _
            }):
                return _
            
    def insert_preset(self, preset: Preset):
        preset._id = self._generateUUID()
        return self._collection.insert_one(preset.Simplified())
    
    def get_all_presets(self):
        return list(self._collection.find())
    
    def get_preset_by_id(self, id: UUID):
        return self._collection.find_one({
            "_id": id
        })
    
    def delete_preset(self, id: UUID):
        return self._collection.delete_one({
            "_id": id
        })