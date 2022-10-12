package eu.kaesebrot.transcodeservice.models;

import eu.kaesebrot.transcodeservice.constants.ETranscodeServiceStatus;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import java.util.UUID;

public class Status {
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
    private ETranscodeServiceStatus StatusCode;

    public Status() {
        StatusCode = ETranscodeServiceStatus.CREATED;
    }
    public Status(ETranscodeServiceStatus statusCode) {
        StatusCode = statusCode;
    }
}
