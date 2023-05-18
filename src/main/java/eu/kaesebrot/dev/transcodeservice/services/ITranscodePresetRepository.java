package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
public interface ITranscodePresetRepository extends JpaRepository<TranscodePreset, UUID> {

}
