package codelens.backend.Session.service;

import codelens.backend.Session.entity.Session;
import codelens.backend.Session.responseEntity.SessionResponse;


public interface SessionService {
    Session createSession(String userId, String sessionName);
    SessionResponse getSession(String userId);
    SessionResponse updateSession(String userId, String sessionId, String sessionName);
    SessionResponse deleteSession(String userId, String sessionId);
    Session getSessionById(String sessionId);
}
