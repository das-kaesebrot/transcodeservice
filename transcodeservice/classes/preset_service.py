from sqlalchemy.orm import Session
from transcodeservice.models.preset import Preset

class PresetService:    
    
    
    def __init__(self, session: Session) -> None:
        self._session = session
            
    def insert_preset(self, preset: Preset):
        result = self._collection.insert_one(preset.Simplified())
        return self.get_preset_by_id(result.inserted_id)
    
    def get_all_presets(self):
        return list(self._collection.find())
    
    def get_preset_by_id(self, id):
        return self._collection.find_one({
            "_id": id
        })
    
    def delete_preset(self, id):
        return self._collection.delete_one({
            "_id": id
        })