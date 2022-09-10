from bson import ObjectId
from transcodeservice.classes.db import DB
from transcodeservice.classes.preset import Preset

class PresetService:

    COLLECTION = "presets"

    def __init__(self):
        db = DB()
        self._collection = db.database[PresetService.COLLECTION]
            
    def insert_preset(self, preset: Preset):
        return self._collection.insert_one(preset.Simplified())
    
    def get_all_presets(self):
        return list(self._collection.find())
    
    def get_preset_by_id(self, id):
        return self._collection.find_one({
            "_id": ObjectId(id)
        })
    
    def delete_preset(self, id):
        return self._collection.delete_one({
            "_id": ObjectId(id)
        })