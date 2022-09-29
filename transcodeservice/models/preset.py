# Definition file for the Preset class, representing a single preset object

import uuid
from .base import Base
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from sqlalchemy import Column, BigInteger, Integer, String, DateTime, Float
from sqlalchemy_utils import UUIDType

class Preset(Base):
    
    __tablename__ = 'preset'
    
    id = Column(UUIDType, primary_key=True, default=uuid.uuid4)
    description = Column(String)
    
    vcodec = Column(String)
    acodec = Column(String)
    vbitrate = Column(BigInteger)
    abitrate = Column(BigInteger)
    format = Column(String(32))
    
    width = Column(Integer)
    height = Column(Integer)
    framerate = Column(Float)
    audiorate = Column(BigInteger)
    crf = Column(Integer)
    
    videofilter = Column(String)
    audiofilter = Column(String)
    
    pix_fmt = Column(String)
        
    created = Column(DateTime, server_default=func.now())
    modified = Column(DateTime, server_default=func.now(), onupdate=func.current_timestamp())
    
    jobs = relationship("TranscodeJob", back_populates="preset")
    
    def __repr__(self):
        return f"Preset(id={self.id!r}, description={self.description!r}, created={self.created!r}, modified={self.modified})"
    
    def to_dict(self) -> dict:
       return {c.name: str(getattr(self, c.name)) for c in self.__table__.columns}
   
    def to_dict_for_update(self) -> dict:
        update_dict = self.to_dict()
        update_dict.pop("modified")
        update_dict.pop("created")
        update_dict.pop("id")
        
        return update_dict
    
class PresetHelper():
    
    @staticmethod
    def from_dict(data: dict) -> Preset:
        job = Preset()
       
        for key, value in data.items():
            setattr(job, key, value)

        return job