package com.agenticai.aegisagent.document;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DocumentVectorStoreInitializer implements ApplicationRunner {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    @Qualifier("documentVectorStore")
    private VectorStore documentVectorStore;

    @Override
    public void run(ApplicationArguments args) {

        List<com.agenticai.aegisagent.document.Document> documents =
                documentRepository.findAll();

        if (documents.isEmpty()) {
            return;
        }

        TokenTextSplitter splitter =
                TokenTextSplitter.builder()
                        .withChunkSize(800)
                        .build();

        for (com.agenticai.aegisagent.document.Document document : documents) {

            if (document.getContent() == null ||
                    document.getContent().isBlank()) {
                continue;
            }

            Document aiDocument =
                    new Document(
                            document.getContent(),
                            Map.of(
                                    "documentId",
                                    document.getId().toString(),
                                    "title",
                                    document.getTitle(),
                                    "category",
                                    document.getCategory()
                            )
                    );

            List<Document> chunks =
                    splitter.split(List.of(aiDocument));

            documentVectorStore.add(chunks);
        }
    }
}