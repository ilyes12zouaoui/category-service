package ilyes.de.categoryservice.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FaviconResource {

    @GetMapping("favicon.ico")
    void returnNoFavicon() {
    }
}
