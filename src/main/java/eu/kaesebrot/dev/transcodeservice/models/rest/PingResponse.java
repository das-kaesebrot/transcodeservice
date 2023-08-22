package eu.kaesebrot.dev.transcodeservice.models.rest;

import java.io.Serializable;

public class PingResponse implements Serializable {
    public final String getData() {
        return "pong";
    }
}
