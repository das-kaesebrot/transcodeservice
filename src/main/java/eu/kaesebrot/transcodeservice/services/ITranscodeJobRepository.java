package eu.kaesebrot.transcodeservice.services;

import eu.kaesebrot.transcodeservice.models.TranscodeJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ITranscodeJobRepository extends JpaRepository<TranscodeJob, UUID> {

}