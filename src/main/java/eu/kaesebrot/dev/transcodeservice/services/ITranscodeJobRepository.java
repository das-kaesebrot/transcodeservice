package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface ITranscodeJobRepository extends JpaRepository<TranscodeJob, Long> {
    void deleteAllByStatus(ETranscodeServiceStatus transcodeStatus);
    void deleteAllByStatusIn(List<ETranscodeServiceStatus> transcodeServiceStatuses);
    Stream<TranscodeJob> getByStatusIn(List<ETranscodeServiceStatus> transcodeStatuses);
    Stream<TranscodeJob> getByPreset(TranscodePreset preset);
    long countByStatus(ETranscodeServiceStatus status);
    void deleteById(Long id);
}