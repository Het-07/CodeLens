package codelens.backend.Session.controller;

import codelens.backend.Session.entity.Session;
import codelens.backend.Session.requestEntity.CreateSessionRequest;
import codelens.backend.Session.requestEntity.UpdateSessionRequest;
import codelens.backend.Session.responseEntity.SessionResponse;
import codelens.backend.Session.service.SessionService;
import codelens.backend.util.HttpStatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/session")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping("/")
    public ResponseEntity<SessionResponse> createSession(@RequestBody CreateSessionRequest req) {
        Session session = sessionService.createSession(req.getUserId(), req.getSessionName());
        SessionResponse sessionResponse = SessionResponse.builder()
                                                .statusCode(HttpStatusCode.CREATED)
                                                .url("/api/v1/session")
                                                .body(Map.of(
                                                        "session", session,
                                                        "message", "Session Created Successfully"
                                                ))
                                                .timestamp(new Date().toString())
                                                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionResponse);
    }

    @GetMapping("/id/{sessionId}")
    public ResponseEntity<SessionResponse> getSessionById(@PathVariable("sessionId") String sessionId) {
        Session session = sessionService.getSessionById(sessionId);
        SessionResponse response = SessionResponse.builder()
                .statusCode(HttpStatusCode.OK)
                .timestamp(new Date().toString())
                .body(Map.of(
                        "session", session,
                        "message", "Session Retrieved Successfully"
                ))
                .url("/api/v1/session")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<SessionResponse> getSessionByUser(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(sessionService.getSession(userId));
    }

    @PutMapping("/")
    public ResponseEntity<SessionResponse> updateSessionById(@RequestBody UpdateSessionRequest updateSessionRequest) {
        return ResponseEntity.ok(sessionService.updateSession(updateSessionRequest.getUserId(), updateSessionRequest.getSessionId(), updateSessionRequest.getSessionName()));
    }

    @DeleteMapping("/{userId}/{sessionId}")
    public ResponseEntity<SessionResponse> deleteSessionById(@PathVariable("userId") String userId, @PathVariable("sessionId") String sessionId) {
        return ResponseEntity.ok(sessionService.deleteSession(userId, sessionId));
    }

}
