package eu.kaesebrot.transcodeservice.models;

import eu.kaesebrot.transcodeservice.constants.ETranscodeServiceStatus;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import java.util.UUID;

public class TranscodeStatus {
    @javax.persistence.Version
    @Column(name = "version")
    private long Version;

    @javax.persistence.Id
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID Id;

    @Column(name = "status_code", nullable = false)
    private ETranscodeServiceStatus StatusEnum;

    public TranscodeStatus() {
        StatusEnum = ETranscodeServiceStatus.CREATED;
    }
    public TranscodeStatus(ETranscodeServiceStatus statusEnum) {
        StatusEnum = statusEnum;
    }

    public ETranscodeServiceStatus getStatusEnum() {
        return StatusEnum;
    }

    public void setStatusEnum(ETranscodeServiceStatus status) {
        if (status.getStatusCode() >= this.StatusEnum.getStatusCode()) {
            this.StatusEnum = status;
            return;
        }

        throw new IllegalArgumentException("");
    }
}
