package com.manish.pragyan;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.manish.pragyan.modules.rag.services.RagService;

@SpringBootTest
class PragyanApplicationTests {

    @Autowired
    RagService ragService;

    @Autowired
    VectorStore vectorStore;

    @Test
    void ingestDocument_storesAndIsSearchable() {
        boolean ok = ragService.ingestDocument(List.of(
                "Pragyan is a RAG application built with Spring AI.",
                "pgvector stores embeddings inside PostgreSQL."));

        assertTrue(ok);

        List<Document> hits = vectorStore.similaritySearch("What is pgvector?");
        assertFalse(hits.isEmpty());
    }
}