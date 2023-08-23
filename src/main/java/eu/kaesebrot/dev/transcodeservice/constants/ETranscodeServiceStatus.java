package eu.kaesebrot.dev.transcodeservice.constants;

public enum ETranscodeServiceStatus {
    CREATED,
    QUEUED,
    RUNNING,
    ABORTED,
    SUCCESS,
    FAILED,
    ;

    public long getStatusCode() {
        return this.ordinal();
    }
}
