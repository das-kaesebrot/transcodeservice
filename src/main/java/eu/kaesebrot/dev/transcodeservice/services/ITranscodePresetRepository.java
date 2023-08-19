package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITranscodePresetRepository extends JpaRepository<TranscodePreset, Long> {

}
