package codelens.backend.services;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import codelens.backend.util.HttpStatusCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import codelens.backend.ShareableLink.linkEntity.ShareableLink;
import codelens.backend.ShareableLink.repository.LinkRepository;
import codelens.backend.ShareableLink.responseEntity.accessLinkResponse;
import codelens.backend.ShareableLink.service.LinkServiceImpl;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @InjectMocks
    private LinkServiceImpl linkService;
    private String validToken;
    private String documentId;

    private final int HOURS_TO_ADD = 12;

    @Value("${backend.url}")
    private String BACKEND_URL;

    /**
     * Test case for generating a shareable link.
     * Should return a valid link and store it in the repository.
     */
    @Test
    void generateShareableLink_ShouldReturnValidLink() {
        String documentId = "sample";
        String expectedToken = UUID.randomUUID().toString();
        ShareableLink mockLink = new ShareableLink(expectedToken, documentId, LocalDateTime.now().plusHours(HOURS_TO_ADD));

        when(linkRepository.save(any(ShareableLink.class))).thenReturn(mockLink);
        String generatedLink = linkService.generateShareableLink(documentId);

        assertThat(generatedLink).isNotNull();
        assertThat(generatedLink).startsWith(BACKEND_URL + "/link/access/");
        verify(linkRepository, times(1)).save(any(ShareableLink.class));
    }

    /**
     * Test case for generating a token in valid UUID format.
     * Should ensure that the generated token is a valid UUID.
     */
    @Test
    void generateShareableLink_ShouldGenerateValidUUIDToken() {
        String documentId = "sample";

        String generatedLink = linkService.generateShareableLink(documentId);
        String token = generatedLink.replace(BACKEND_URL + "/link/access/", "");

        assertThat(UUID.fromString(token)).isInstanceOf(UUID.class);
    }

    /**
     * Test case for handling invalid document IDs.
     * Should throw an exception when document ID is empty or null.
     */
    @Test
    void generateShareableLink_ShouldThrowExceptionIfDocumentIdIsEmptyOrNull() {
        String emptyDocumentId = "";

        assertThrows(IllegalArgumentException.class, () -> linkService.generateShareableLink(emptyDocumentId).equals("Document ID cannot be empty or null"));
        assertThrows(IllegalArgumentException.class, () -> linkService.generateShareableLink(null).equals("Document ID cannot be null"));

        verify(linkRepository, never()).save(any(ShareableLink.class));
    }

    /**
     * Test case to ensure unique token generation.
     * Should generate a different token for each request.
     */
    @Test
    void generateShareableLink_ShouldGenerateUniqueTokenEachTime() {
        String documentId = "sample";

        String link1 = linkService.generateShareableLink(documentId);
        String link2 = linkService.generateShareableLink(documentId);

        String token1 = link1.replace("http://localhost:8080/api/v1/link/access/", "");
        String token2 = link2.replace("http://localhost:8080/api/v1/link/access/", "");

        assertThat(token1).isNotEqualTo(token2);
    }

    /**
     * Test case for ensuring repository interaction.
     * Should store the generated link in the repository.
     */
    @Test
    void generateShareableLink_ShouldSaveLinkInRepository() {
        String documentId = "sample";

        linkService.generateShareableLink(documentId);
        verify(linkRepository, times(1)).save(any(ShareableLink.class));
    }

    /**
     * Test case for retrieving a valid shareable link.
     * Should return the link if it exists and is not expired.
     */
    @Test
    void getShareableLink_ShouldReturnValidLink_IfNotExpired() {
        ShareableLink validShareableLink = new ShareableLink(validToken, documentId, LocalDateTime.now().plusHours(HOURS_TO_ADD)); // Not expired
        when(linkRepository.findByToken(validToken)).thenReturn(Optional.of(validShareableLink));

        Optional<ShareableLink> retrievedLink = linkService.getShareableLink(validToken);

        assertThat(retrievedLink).isPresent();
        assertThat(retrievedLink.get().getToken()).isEqualTo(validToken);
        assertThat(retrievedLink.get().isExpired()).isFalse();
    }


    /**
     * Test case for retrieving a non-existing token.
     * Should return empty if the token does not exist.
     */
    @Test
    void getShareableLink_ShouldReturnEmpty_IfTokenDoesNotExist() {
        Optional<ShareableLink> retrievedLink = linkService.getShareableLink("invalid-token");
        assertThat(retrievedLink).isEmpty();
    }

    /**
     * Test case for retrieving an expired shareable link.
     * Should return empty if the link exists but is expired.
     */
    @Test
    void getShareableLink_ShouldReturnEmpty_IfLinkIsExpired() {
        ShareableLink expiredShareableLink = new ShareableLink(UUID.randomUUID().toString(), documentId, LocalDateTime.now().minusHours(HOURS_TO_ADD)); // Expired
        when(linkRepository.findByToken(expiredShareableLink.getToken())).thenReturn(Optional.of(expiredShareableLink));
        Optional<ShareableLink> retrievedLink = linkService.getShareableLink(expiredShareableLink.getToken());
        assertThat(retrievedLink).isEmpty();
    }

    /**
     * Test case for accessing a valid shareable link.
     * Should return a valid accessLinkResponse when the link exists and is not expired.
     */
    @Test
    void accessShareableLink_ShouldReturnValidResponse_WhenTokenIsValid() {
        String token = UUID.randomUUID().toString();
        String documentId = "sampleDoc";

        ShareableLink validLink = new ShareableLink(token, documentId, LocalDateTime.now().plusHours( HOURS_TO_ADD));
        when(linkRepository.findByToken(token)).thenReturn(Optional.of(validLink));

        try (MockedStatic<ServletUriComponentsBuilder> builderMock = mockStatic(ServletUriComponentsBuilder.class)) {
            ServletUriComponentsBuilder builder = mock(ServletUriComponentsBuilder.class);
            builderMock.when(ServletUriComponentsBuilder::fromCurrentContextPath).thenReturn(builder);
            when(builder.path(anyString())).thenReturn(builder);
            when(builder.queryParam(anyString(), Optional.ofNullable(any()))).thenReturn(builder);
            when(builder.toUriString()).thenReturn("http://localhost:8080/api/v1/download?messageId=" + documentId);

            accessLinkResponse response = linkService.accessShareableLink(token);
            Map<String, String> bodyMap = (Map<String, String>) response.getBody();

            assertNotNull(response);
            assertEquals("http://localhost:8080/api/v1/download?messageId=" + documentId, response.getUrl());
            assertEquals("downloaded successfully", bodyMap.get("message"));
            assertEquals("/link/access/" + token, bodyMap.get("redirectedFrom"));
            assertEquals(HttpStatusCode.MOVED_PERMANENTLY, response.getStatusCode());
            assertNotNull(response.getTimestamp());
        }
    }

    /**
     * Test case for accessing a shareable link that is expired or does not exist.
     * Should throw NoSuchElementException with the appropriate message.
     */
    @Test
    void accessShareableLink_ShouldThrowNoSuchElementException_WhenTokenNotFoundOrExpired() {
        String token = UUID.randomUUID().toString();
        // Simulate that the token is not found or the link is expired
        when(linkRepository.findByToken(token)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> linkService.accessShareableLink(token));
        assertThat(exception.getMessage()).isEqualTo("The Link is not valid, Please check your link.");
    }



}
