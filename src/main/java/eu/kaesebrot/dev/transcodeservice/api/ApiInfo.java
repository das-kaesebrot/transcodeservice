package eu.kaesebrot.dev.transcodeservice.api;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.stereotype.Component;

@Component
@OpenAPIDefinition(
        info = @Info(title = "TranscodeService API", version = "1.0",
                description = "REST API for managing transcoding presets and jobs"
                /*license = @License(name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html")*/
        )
)
public class ApiInfo {
}
