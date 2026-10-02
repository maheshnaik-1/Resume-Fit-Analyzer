package com.resumeanalyzer.pdf;

import com.resumeanalyzer.component.PdfTextExtractor;
import com.resumeanalyzer.exception.InvalidFileException;
import com.resumeanalyzer.exception.PdfParsingException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PdfTextExtractorTest {

    private PdfTextExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new PdfTextExtractor();
    }

    private byte[] createPdf(String... pageTexts) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            for (String text : pageTexts) {
                PDPage page = new PDPage();
                doc.addPage(page);
                try (PDPageContentStream stream = new PDPageContentStream(doc, page)) {
                    stream.beginText();
                    stream.setFont(font, 12);
                    stream.newLineAtOffset(50, 700);
                    stream.showText(text);
                    stream.endText();
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        }
    }

    private byte[] createEncryptedPdf(String ownerPassword, String userPassword, String text) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream stream = new PDPageContentStream(doc, page)) {
                stream.beginText();
                stream.setFont(font, 12);
                stream.newLineAtOffset(50, 700);
                stream.showText(text);
                stream.endText();
            }

            AccessPermission ap = new AccessPermission();
            StandardProtectionPolicy spp = new StandardProtectionPolicy(ownerPassword, userPassword, ap);
            spp.setEncryptionKeyLength(128);
            doc.protect(spp);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        }
    }

    @Test
    @DisplayName("1. Valid single-page PDF extracts correctly")
    void testValidSinglePagePdfExtraction() throws IOException {
        byte[] pdfBytes = createPdf("John Doe - Senior Software Engineer with Java and Spring Boot expertise.");
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", pdfBytes);

        String extracted = extractor.extractText(file);
        assertNotNull(extracted);
        assertTrue(extracted.contains("John Doe"));
        assertTrue(extracted.contains("Senior Software Engineer"));
    }

    @Test
    @DisplayName("2. Multi-page PDF text is joined with single space delimiter")
    void testMultiPagePdfJoining() throws IOException {
        byte[] pdfBytes = createPdf(
                "Page One: Education B.Tech Computer Science.",
                "Page Two: Experience at Google building microservices."
        );
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", pdfBytes);

        String extracted = extractor.extractText(file);
        assertNotNull(extracted);
        assertTrue(extracted.contains("Page One: Education B.Tech Computer Science."));
        assertTrue(extracted.contains("Page Two: Experience at Google building microservices."));
    }

    @Test
    @DisplayName("3. Empty file throws InvalidFileException")
    void testEmptyFileThrowsException() {
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", new byte[0]);

        InvalidFileException ex = assertThrows(InvalidFileException.class, () -> extractor.extractText(file));
        assertEquals("The uploaded file is empty. Please upload a valid resume PDF.", ex.getMessage());
    }

    @Test
    @DisplayName("4. Oversized file (> 5 MB) throws InvalidFileException")
    void testOversizedFileThrowsException() {
        byte[] largeBytes = new byte[(int) (PdfTextExtractor.MAX_FILE_SIZE + 10)];
        // Fill start with %PDF so it passes magic check if reached
        largeBytes[0] = 0x25;
        largeBytes[1] = 0x50;
        largeBytes[2] = 0x44;
        largeBytes[3] = 0x46;

        MockMultipartFile file = new MockMultipartFile("file", "large.pdf", "application/pdf", largeBytes);

        InvalidFileException ex = assertThrows(InvalidFileException.class, () -> extractor.extractText(file));
        assertEquals("File size exceeds the 5 MB limit. Please upload a smaller PDF.", ex.getMessage());
    }

    @Test
    @DisplayName("5. Non-PDF extension throws InvalidFileException")
    void testNonPdfExtensionThrowsException() {
        byte[] fakeBytes = "%PDF-1.4 Fake PDF Content Here".getBytes();
        MockMultipartFile fileDocx = new MockMultipartFile("file", "resume.docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", fakeBytes);
        MockMultipartFile fileTxt = new MockMultipartFile("file", "resume.txt", "text/plain", fakeBytes);

        InvalidFileException exDocx = assertThrows(InvalidFileException.class, () -> extractor.extractText(fileDocx));
        assertEquals("Invalid file format. Only PDF documents (.pdf) are supported.", exDocx.getMessage());

        InvalidFileException exTxt = assertThrows(InvalidFileException.class, () -> extractor.extractText(fileTxt));
        assertEquals("Invalid file format. Only PDF documents (.pdf) are supported.", exTxt.getMessage());
    }

    @Test
    @DisplayName("6. Missing %PDF magic bytes throws InvalidFileException")
    void testMissingMagicBytesThrowsException() {
        byte[] nonPdfBytes = "Plain text pretending to be pdf with length more than 20 chars".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "fake.pdf", "application/pdf", nonPdfBytes);

        InvalidFileException ex = assertThrows(InvalidFileException.class, () -> extractor.extractText(file));
        assertEquals("Invalid PDF file. The uploaded file is not a valid PDF document.", ex.getMessage());
    }

    @Test
    @DisplayName("7. PDF with less than 10 extracted characters throws PdfParsingException")
    void testShortOrScannedPdfThrowsException() throws IOException {
        byte[] shortPdfBytes = createPdf("Hi");
        MockMultipartFile file = new MockMultipartFile("file", "short.pdf", "application/pdf", shortPdfBytes);

        PdfParsingException ex = assertThrows(PdfParsingException.class, () -> extractor.extractText(file));
        assertEquals("No readable text found in the PDF. Scanned or image-only documents without an embedded text layer are not supported.", ex.getMessage());
    }

    @Test
    @DisplayName("8. Corrupted PDF byte stream throws PdfParsingException")
    void testCorruptedPdfThrowsException() {
        byte[] corruptedBytes = new byte[]{0x25, 0x50, 0x44, 0x46, 0x00, 0x01, 0x02, 0x03, 0x04};
        MockMultipartFile file = new MockMultipartFile("file", "corrupt.pdf", "application/pdf", corruptedBytes);

        PdfParsingException ex = assertThrows(PdfParsingException.class, () -> extractor.extractText(file));
        assertEquals("Unable to parse the PDF file. The file may be corrupted, encrypted, or password-protected.", ex.getMessage());
    }

    @Test
    @DisplayName("9. Encrypted/password-protected PDF throws PdfParsingException")
    void testEncryptedPdfThrowsException() throws IOException {
        byte[] encryptedPdfBytes = createEncryptedPdf("ownerSecretPass", "userSecretPass", "Secret confidential resume text for encryption test.");
        MockMultipartFile file = new MockMultipartFile("file", "encrypted.pdf", "application/pdf", encryptedPdfBytes);

        PdfParsingException ex = assertThrows(PdfParsingException.class, () -> extractor.extractText(file));
        assertEquals("Unable to parse the PDF file. The file may be corrupted, encrypted, or password-protected.", ex.getMessage());
    }
}
