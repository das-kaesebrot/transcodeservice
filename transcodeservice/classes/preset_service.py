from sqlalchemy.orm import Session
from transcodeservice.models.preset import Preset

class PresetService:    
    
    
    def __init__(self, session: Session) -> None:
        self._session = session
            
    def insert_preset(self, preset: Preset):
        self._session.add(preset)
        self._session.commit()
        self._session.refresh(preset)
        
        return preset
        
    def update_preset(self, update_dict: dict, preset_id: Preset.id):        
        self._session.query(Preset).\
            filter_by(id=preset_id).update(update_dict)
        
        self._session.commit()
        return self._session.get(Preset, preset_id)
    
    def get_all_presets(self):
        return list(self._session.query(Preset).all())
    
    def get_preset_by_id(self, id):
        return self._session.get(Preset, id)
    
    def delete_preset(self, id):
        to_be_deleted = self.get_preset_by_id(id)
        self._session.delete(to_be_deleted)
        self._session.commit()