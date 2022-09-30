# Definition file for the TranscodeJob class, representing a single job object
import enum
import uuid
from .base import Base
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from sqlalchemy import Table, Column, Integer, String, Enum, DateTime, ForeignKey
from sqlalchemy_utils import UUIDType

class TranscodeJobStatus(enum.IntEnum):
    CREATED:    int = 0
    STARTED:    int = 1 # used to hand over job to event watcher
    RUNNING:    int = 2
    ABORTED:    int = 3
    SUCCESS:    int = 4
    FAILED:     int = 5
    
    def __str__(self):
        return self.name
    

class TranscodeJob(Base):
    
    __tablename__ = 'transcodejob'
    
    id = Column(UUIDType, primary_key=True, default=uuid.uuid4)
    in_file = Column(String, nullable=False)
    out_folder = Column(String, nullable=False)
    status = Column(Enum(TranscodeJobStatus))
    created = Column(DateTime, server_default=func.now())
    modified = Column(DateTime, server_default=func.now(), onupdate=func.current_timestamp())
    preset_id = Column(UUIDType, ForeignKey('preset.id'))
    preset = relationship("Preset", back_populates="jobs")
    
    def __init__(self, in_file, out_folder, preset_id) -> None:
        self.in_file = in_file
        self.out_folder = out_folder
        self.status = TranscodeJobStatus.CREATED
        self.preset_id = preset_id
    
    def __repr__(self):
        return f"TranscodeJob(id={self.id!r}, in_file={self.in_file!r}, out_folder={self.out_folder!r}, status={self.status!r}, created={self.created!r}, modified={self.modified!r}, preset_id={self.preset_id!r})"

    def run(self):
        if self.status == TranscodeJobStatus.CREATED:
            self.status = TranscodeJobStatus.STARTED
    
    def abort(self):
        if self.status == TranscodeJobStatus.RUNNING:
            self.status == TranscodeJobStatus.ABORTED

    def to_dict(self):
       return {c.name: str(getattr(self, c.name)) for c in self.__table__.columns}
   