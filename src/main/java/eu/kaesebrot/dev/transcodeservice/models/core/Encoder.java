package eu.kaesebrot.dev.transcodeservice.models.core;

import eu.kaesebrot.dev.transcodeservice.constants.EEncoderType;

public record Encoder(String name, String longName, EEncoderType type) {

}
