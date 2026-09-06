package com.agenticai.aegisagent.audit;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditVectorSearchService {

    @Autowired
    @Qualifier("auditVectorStore")
    private VectorStore auditVectorStore;

    public List<Document> search(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        return auditVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(5)
                        .build()
        );
    }
}
