package codelens.backend.services;


import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import codelens.backend.Session.responseEntity.SessionResponse;
import org.bson.types.ObjectId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import codelens.backend.Session.entity.Session;
import codelens.backend.Session.repository.SessionRepository;
import codelens.backend.Session.requestEntity.CreateSessionRequest;

import codelens.backend.Session.service.SessionServiceImpl;
import codelens.backend.User.User;
import codelens.backend.User.UserRepository;


@ExtendWith(MockitoExtension.class)
public class SessionServiceTests {
    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionServiceImpl sessionService;


    private User user;
    private Session session;


    // Runs before each test to initialize mock objects
    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(new ObjectId());

        session = Session.builder()
                .id(new ObjectId().toString())
                .userId(user.getId().toString())
                .sessionName("Test Session")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    /**
     *  Test Case: successful creation of a session
     */
  @Test
    public void CreateSessionResponse_ReturnsCreatedSession() {
        CreateSessionRequest request = new CreateSessionRequest(user.getId().toString(), "Test Session");
        when(userRepository.findById(any(ObjectId.class))).thenReturn(Optional.of(user));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        Session response = sessionService.createSession(session.getUserId() , session.getSessionName());

        assertThat(response).isNotNull();

        Mockito.verify(userRepository , times(1)).findById(any(ObjectId.class));
        Mockito.verify((sessionRepository) , times(1)).save(any(Session.class));
    }

    /**
     * Test Case: for Retrieving the session response
     */
    @Test
    public void GetSessionResponse_RetrievesSession() {
        when((sessionRepository.findByUserIdOrderByCreatedAtDesc(any(String.class)))).thenReturn(List.of(session));

        SessionResponse response = sessionService.getSession(user.getId().toString());

        assertThat(response.getBody()).isNotNull();
        Mockito.verify(sessionRepository , times(1)).findByUserIdOrderByCreatedAtDesc(any(String.class));
    }

    @Test
    public void GetSessionByIdResponse_RetrievesSession() {
        when(sessionRepository.findById(any(ObjectId.class))).thenReturn(Optional.of(session));

        Session response = sessionService.getSessionById(session.getId());

        assertThat(response).isNotNull();
        Mockito.verify(sessionRepository , times(1)).findById(any(ObjectId.class));
    }

    @Test
    public void GetSessionByIdResponse_SessionNotFound() {
        when(sessionRepository.findById(any(ObjectId.class))).thenReturn(Optional.empty());

        assertThrows(Error.class, () -> sessionService.getSessionById(session.getId()));

        Mockito.verify(sessionRepository , times(1)).findById(any(ObjectId.class));
    }

    /**
     * Test Case: Update the Session Response
     */

    @Test
    public void UpdateSessionResponse_ReturnsUpdatedSession() {
        when(userRepository.findById(any(ObjectId.class))).thenReturn(Optional.of(user));
        when(sessionRepository.findById(any(ObjectId.class))).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(Session.class))).thenReturn(session);

        SessionResponse response = sessionService.updateSession(session.getUserId() , session.getId() , session.getSessionName());

        assertThat(response.getBody()).isNotNull();
        Mockito.verify(userRepository , times(1)).findById(any(ObjectId.class));
        Mockito.verify(sessionRepository , times(1)).findById(any(ObjectId.class));
        Mockito.verify(sessionRepository, times(1)).save(any(Session.class));
    }

    /**
     * Test Case: Delete the Session
     */
    @Test
    public void DeleteSessionResponse_DeletesSession() {
        String sessionId = session.getId();
        String sessionUserId = user.getId().toString();

        when(userRepository.findById(any(ObjectId.class))).thenReturn(Optional.of(user));
        when(sessionRepository.findById(any(ObjectId.class))).thenReturn(Optional.of(session));
        doNothing().when(sessionRepository).deleteById(any(ObjectId.class));

        SessionResponse response = sessionService.deleteSession(sessionUserId , sessionId);

        assertEquals(204, response.getStatusCode());
        verify(sessionRepository, times(1)).deleteById(any(ObjectId.class));
    }
}
