package eu.kaesebrot.transcodeservice.services;

import eu.kaesebrot.transcodeservice.models.TranscodePreset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
public interface ITranscodePresetRepository extends JpaRepository<TranscodePreset, UUID> {

}
