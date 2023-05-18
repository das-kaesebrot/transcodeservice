package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ITranscodeJobRepository extends JpaRepository<TranscodeJob, UUID> {

}