package ilyes.de.categoryservice.resource;

import ilyes.de.categoryservice.config.aop.annotation.WithResourceLogs;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

@RestController
@RequestMapping("/api/app/v1/general")
public class GeneralResource {

    @GetMapping(value="/openapi",produces = "application/x-yaml")
    public ResponseEntity getOpenApi() throws IOException {
        File file = ResourceUtils.getFile("classpath:openapi/open-api.yaml");
        if (file.exists()) {
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType("application/x-yaml"))
                    .header("Content-Disposition", "attachment; filename=category-service-open-api.yaml")
                    .body(new String(Files.readAllBytes(file.toPath())));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value="/throwerror")
    @WithResourceLogs(logType = "CATEGORY_THROW")
    public Map<String,Object> throwError() {
        throw new RuntimeException();
    }
}
