package com.manish.pragyan.modules.rag.services;

import org.springframework.ai.document.Document;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

public interface RagService {
	public String getResponse(String query, UUID sessionID);
	
	public Flux<String> getStreamResponse(String query, UUID sessionID);

    public boolean ingestDocument(List<String> documents);
}
