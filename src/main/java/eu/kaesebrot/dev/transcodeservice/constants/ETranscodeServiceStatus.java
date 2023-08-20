package eu.kaesebrot.dev.transcodeservice.constants;

public enum ETranscodeServiceStatus {
    CREATED,
    STARTED,
    RUNNING,
    ABORTED,
    SUCCESS,
    FAILED,
    ;

    public long getStatusCode() {
        return this.ordinal();
    }
}
