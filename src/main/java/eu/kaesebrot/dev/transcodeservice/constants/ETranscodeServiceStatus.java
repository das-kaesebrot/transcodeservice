package eu.kaesebrot.dev.transcodeservice.constants;

public enum ETranscodeServiceStatus {
    CREATED (0),
    STARTED (1),
    RUNNING (2),
    ABORTED (3),
    SUCCESS (4),
    FAILED (5),
    ;

    private final long statusCode;

    public long getStatusCode() {
        return statusCode;
    }

    ETranscodeServiceStatus(long statusCode) {
        this.statusCode = statusCode;
    }
}
