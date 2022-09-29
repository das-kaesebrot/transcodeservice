# Definition file for the TranscodeJob class, representing a single job object
import enum
import uuid
from .base import Base
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func
from sqlalchemy import Table, Column, Integer, String, Enum, TIMESTAMP, ForeignKey
from sqlalchemy_utils import UUIDType

class TranscodeJobStatus(enum.Enum):
    CREATED = 1
    STARTED = 2 # used to hand over job to event watcher
    RUNNING = 3
    ABORTED = 4
    SUCCESS = 5
    FAILED = 6

class TranscodeJob(Base):
    
    __tablename__ = 'transcodejob'
    
    id = Column(UUIDType, primary_key=True, default=uuid.uuid4)
    in_file = Column(String, nullable=False)
    out_folder = Column(String, nullable=False)
    status = Column(Enum(TranscodeJobStatus))
    created = Column(TIMESTAMP, server_default=func.now())
    modified = Column(TIMESTAMP, server_default=func.now(), onupdate=func.current_timestamp())
    preset_id = Column(UUIDType, ForeignKey('preset.id'))
    preset = relationship("Preset", back_populates="jobs")
    
    def __repr__(self):
        return f"TranscodeJob(id={self.id!r}, in_file={self.in_file!r}, out_folder={self.out_folder!r}, status={self.status!r}, created={self.created!r}, modified={self.modified!r}, preset_id={self.preset_id!r})"

    def run(self):
        if self.status == TranscodeJobStatus.CREATED:
            self.status = TranscodeJobStatus.STARTED
    
    def abort(self):
        if self.status == TranscodeJobStatus.RUNNING:
            self.status == TranscodeJobStatus.ABORTED
