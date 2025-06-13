package codelens.backend.Session.service;

import codelens.backend.Session.entity.Session;
import codelens.backend.Session.repository.SessionRepository;
import codelens.backend.Session.responseEntity.SessionResponse;
import codelens.backend.User.UserRepository;
import codelens.backend.util.HttpStatusCode;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    /**
     * Creates a new session for the given user.
     *
     * @param userId The ID of the user creating the session.
     * @param sessionName The name of the new session.
     * @return The created session object.
     */
    @Override
    public Session createSession(String userId, String sessionName) {

        userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new Error("User not found"));

        Session session = Session.builder()
                        .userId(userId)
                        .sessionName(sessionName)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

        sessionRepository.save(session);

        return session;
    }

    /**
     * Retrieves all sessions for a given user.
     *
     * @param userId The ID of the user whose sessions are to be retrieved.
     * @return A SessionResponse containing session details.
     */
    @Override
    public SessionResponse getSession(String userId) {
        List<Session> sessions = sessionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return SessionResponse.builder()
                .statusCode(HttpStatusCode.OK)
                .timestamp(new Date().toString())
                .body(Map.of(
                    "session", sessions,
                        "message", "Session Retrieved Successfully"
                ))
                .url("/api/v1/session")
                .build();
    }

    /**
     * Retrieves a session by its ID.
     *
     * @param sessionId The ID of the session to be retrieved.
     * @return The session object.
     */
    @Override
    public Session getSessionById(String sessionId) {
        return sessionRepository.findById(new ObjectId(sessionId)).orElseThrow(() -> new Error("Session not found"));
    }

    /**
     * Updates the name of an existing session.
     *
     * @param userId The ID of the user updating the session.
     * @param sessionId The ID of the session to be updated.
     * @param sessionName The new name for the session.
     * @return A SessionResponse containing the updated session details.
     */
    @Override
    public SessionResponse updateSession(String userId, String sessionId, String sessionName) {
        userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new Error("User Not Found"));
        Session session = sessionRepository.findById(new ObjectId(sessionId)).orElseThrow(() -> new Error("Session Not Found"));

        session.setSessionName(sessionName);
        session.setUpdatedAt(Instant.now());
        sessionRepository.save(session);

        return SessionResponse.builder()
                .url("/api/v1/session")
                .body(Map.of(
                        "session", session
                ))
                .timestamp(new Date().toString())
                .statusCode(HttpStatusCode.NO_CONTENT)
                .build();
    }

    /**
     * Deletes an existing session.
     *
     * @param userId The ID of the user deleting the session.
     * @param sessionId The ID of the session to be deleted.
     * @return A SessionResponse confirming the deletion.
     */
    @Override
    public SessionResponse deleteSession(String userId, String sessionId) {
        userRepository.findById(new ObjectId(userId)).orElseThrow(() -> new Error("User Not Found"));
        sessionRepository.findById(new ObjectId(sessionId)).orElseThrow(() -> new Error("Session Not Found"));
        sessionRepository.deleteById(new ObjectId(sessionId));
        return SessionResponse.builder()
                .url("/api/v1/session")
                .timestamp(new Date().toString())
                .body(Map.of(
                        "message", "session deleted successfully"
                ))
                .statusCode(HttpStatusCode.NO_CONTENT)
                .build();
    }
}
