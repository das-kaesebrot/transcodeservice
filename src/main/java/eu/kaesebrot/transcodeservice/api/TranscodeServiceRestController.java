package eu.kaesebrot.transcodeservice.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TranscodeServiceRestController {

    @GetMapping(value = "ping", produces = MediaType.APPLICATION_JSON_VALUE)
    public String Ping() {
        return "pong";
    }
}
