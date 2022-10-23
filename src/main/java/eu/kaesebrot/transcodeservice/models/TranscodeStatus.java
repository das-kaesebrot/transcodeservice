package eu.kaesebrot.transcodeservice.models;

import eu.kaesebrot.transcodeservice.constants.ETranscodeServiceStatus;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "transcode_status")
public class TranscodeStatus implements Serializable {
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