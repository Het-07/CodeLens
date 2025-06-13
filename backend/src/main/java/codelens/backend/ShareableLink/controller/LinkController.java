package codelens.backend.ShareableLink.controller;

import codelens.backend.ShareableLink.responseEntity.accessLinkResponse;
import codelens.backend.ShareableLink.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/link")
public class LinkController {

    private final LinkService linkService;
    @Autowired
    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateShareableLink(@RequestParam String documentId) {
        String link = linkService.generateShareableLink(documentId);
        Map<String, String> response = new HashMap<>();
        response.put("link", link);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/access/{token}")
    public ResponseEntity<?> accessLink(@PathVariable String token) {
        accessLinkResponse response = linkService.accessShareableLink(token);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", response.getUrl())
                .build();
    }
}
