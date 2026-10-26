package com.manish.pragyan.modules.rag.services.impls;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.manish.pragyan.modules.rag.services.RagService;

import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@Service
public class RagServiceImpl implements RagService {
	
	private final ChatClient chatClient;
    private final VectorStore vectorStore;
	
	public RagServiceImpl(ChatClient chatClient,
                          VectorStore vectorStore) {
		this.chatClient = chatClient;
        this.vectorStore = vectorStore;
	}
	
	@Override
	public String getResponse(String query, UUID sessionID) {
		return chatClient.prompt()
				.user(query)
                .advisors(advisorSpec -> advisorSpec
                        .param(ChatMemory.CONVERSATION_ID, sessionID))
                .advisors(QuestionAnswerAdvisor.builder(vectorStore).build())
				.call()
				.content();
	}
	
	@Override
	public Flux<String> getStreamResponse(String query, UUID sessionID) {
		return chatClient.prompt()
				.user(query)
                .advisors(advisorSpec -> advisorSpec
                        .param(ChatMemory.CONVERSATION_ID, sessionID))
                .advisors(QuestionAnswerAdvisor.builder(vectorStore).build())
				.stream()
				.content();
	}

    @Override
    public boolean ingestDocument(List<String> documents) {
        try {
            List<Document> docs = documents.stream()
                    .map(Document::new)
                    .toList();
            vectorStore.add(docs);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

}
