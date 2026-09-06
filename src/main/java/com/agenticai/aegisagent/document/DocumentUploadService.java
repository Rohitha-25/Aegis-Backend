package com.agenticai.aegisagent.document;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class DocumentUploadService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    @Qualifier("documentVectorStore")
    private VectorStore documentVectorStore;

    private final Tika tika = new Tika();

    public com.agenticai.aegisagent.document.Document uploadDocument(MultipartFile file, String category) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty.");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name is missing.");
        }

        try {
            // 1. Extract text from the uploaded file
            String text = tika.parseToString(
                    file.getInputStream()
            );

            if (text == null || text.isBlank()) {
                throw new IllegalArgumentException(
                        "No readable text was found in the document."
                );
            }

            // 2. Save the document in the Aegis database
            com.agenticai.aegisagent.document.Document document =
                    new com.agenticai.aegisagent.document.Document();

            document.setTitle(removeExtension(fileName));
            document.setContent(text);
            document.setCategory(category);

            document = documentRepository.save(document);

            // 3. Create Spring AI document with metadata
            Document aiDocument = new Document(
                    text,
                    Map.of(
                            "documentId",
                            document.getId().toString(),

                            "title",
                            document.getTitle(),

                            "category",
                            category
                    )
            );

            // 4. Split the document into smaller chunks
            TokenTextSplitter splitter =
                    TokenTextSplitter.builder()
                            .withChunkSize(800)
                            .build();

            List<Document> chunks =
                    splitter.split(List.of(aiDocument));

            // 5. Generate embeddings and store vectors
            documentVectorStore.add(chunks);
            return document;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to process the document: "
                    + e.getMessage(), e
            );
        }
    }

    private String removeExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex == -1) {
            return fileName;
        }

        return fileName.substring(0, dotIndex);
    }
}
