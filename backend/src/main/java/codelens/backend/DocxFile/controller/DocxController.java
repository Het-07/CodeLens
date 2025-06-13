package codelens.backend.DocxFile.controller;

import codelens.backend.DocxFile.entity.Docx;
import codelens.backend.DocxFile.repository.DocxRepository;
import codelens.backend.DocxFile.responseEntity.DownloadDocxResponse;
import codelens.backend.DocxFile.service.DocxService;
import codelens.backend.util.HttpStatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/download")
@RequiredArgsConstructor
public class DocxController {
    private final DocxRepository docxRepository;
    private final DocxService docxService;

    @GetMapping("/{messageId}")
    public ResponseEntity<DownloadDocxResponse> downloadFileAsBase64(@PathVariable("messageId") String messageId) throws IOException {


        Docx docxDocument = docxService.getDocx(messageId);

        if (docxDocument != null && docxDocument.getDocxFile() != null) {
            byte[] docxBytes = docxDocument.getDocxFile();

            // Convert byte array to Base64
            String base64Encoded = Base64.getEncoder().encodeToString(docxBytes);

            DownloadDocxResponse downloadDocxResponse = DownloadDocxResponse.builder()
                    .url("/api/v1/download")
                    .statusCode(HttpStatusCode.OK)
                    .body(Map.of(
                            "response", base64Encoded,
                            "message", "Docx file received successfully"
                    ))
                    .timestamp(new Date().toString())
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(downloadDocxResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
