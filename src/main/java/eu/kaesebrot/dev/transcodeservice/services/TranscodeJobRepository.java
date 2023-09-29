package eu.kaesebrot.dev.transcodeservice.services;

import eu.kaesebrot.dev.transcodeservice.constants.ETranscodeServiceStatus;
import eu.kaesebrot.dev.transcodeservice.models.TranscodeJob;
import eu.kaesebrot.dev.transcodeservice.models.TranscodePreset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Repository
public interface TranscodeJobRepository extends JpaRepository<TranscodeJob, Long> {
    void deleteAllByStatus(ETranscodeServiceStatus transcodeStatus);
    void deleteAllByStatusIn(List<ETranscodeServiceStatus> transcodeServiceStatuses);
    Stream<TranscodeJob> getByStatusIn(List<ETranscodeServiceStatus> transcodeStatuses);
    Stream<TranscodeJob> getByPreset(TranscodePreset preset);
    long countByStatus(ETranscodeServiceStatus status);
    void deleteById(Long id);
    @Modifying
    @Query("update TranscodeJob u set u.status = :status where u.id = :id")
    void updateStatus(@Param(value = "id") long id, @Param(value = "status") ETranscodeServiceStatus status);
    @Query("select t.status from TranscodeJob t where t.id = :id")
    ETranscodeServiceStatus getStatus(@Param(value = "id") long id);
    @Query("select t.id, t.status from TranscodeJob t where t in :ids")
    List<Object[]> findStatuses(@Param(value = "ids") Set<Long> ids);
}