package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TranscodePresetRepository extends JpaRepository<TranscodePreset, Long> {
    Optional<TranscodePreset> findByJobsContains(TranscodeJob job);
}
