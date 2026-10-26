package com.manish.pragyan.modules.rag.services.impls;

import java.util.List;
import java.util.UUID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.manish.pragyan.modules.rag.dtos.ChatRequestDto;
import com.manish.pragyan.modules.rag.dtos.ChatResponseDto;
import com.manish.pragyan.modules.rag.dtos.ChatStreamEvent;
import com.manish.pragyan.modules.rag.entities.ChatEntity;
import com.manish.pragyan.modules.rag.entities.ChatType;
import com.manish.pragyan.modules.rag.entities.SessionEntity;
import com.manish.pragyan.modules.rag.mappers.ChatMapper;
import com.manish.pragyan.modules.rag.repositories.ChatRepository;
import com.manish.pragyan.modules.rag.repositories.SessionRepository;
import com.manish.pragyan.modules.rag.services.ChatService;
import com.manish.pragyan.modules.rag.services.RagService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ChatServiceImpl implements ChatService {
	private final ChatRepository chatRepository;
	private final SessionRepository sessionRepository;
	private final ChatMapper chatMapper;
	private final RagService ragService;
	
	public ChatServiceImpl(ChatClient chatClient,
			ChatRepository chatRepository,
			SessionRepository sessionRepository,
			ChatMapper chatMapper,
			RagService ragService) {
		this.chatRepository = chatRepository;
		this.sessionRepository = sessionRepository;
		this.chatMapper = chatMapper;
		this.ragService = ragService;
	}
	
	@Override
	public List<ChatResponseDto> getChatBySessionID(UUID seesionID) {
		return chatRepository.findBySessionEntity_IdOrderByCreatedAtDesc(seesionID).stream()
				.map(chatMapper::entityToDto)
				.toList();
	}
	
	@Override
	public ChatResponseDto chat(UUID sessionID, ChatRequestDto chatRequestDto) {
		SessionEntity sessionEntity = sessionRepository.getReferenceById(sessionID);
		String query = chatRequestDto.query();
		
		// User Chat
		ChatEntity userChat = new ChatEntity(
				ChatType.USER,
				query,
				sessionEntity
			);
		
		chatRepository.save(userChat);
		
		// LLM Call
		String response = ragService.getResponse(query, sessionID);
		
		// AI Chat
		ChatEntity AIResponse = new ChatEntity(
				ChatType.AI,
				response,
				sessionEntity
			);
		
		return chatMapper.entityToDto(chatRepository.save(AIResponse));
	}
	
	@Override
	public Flux<ChatStreamEvent> chatStream(UUID sessionID, ChatRequestDto chatRequestDto) {
		SessionEntity sessionEntity = sessionRepository.getReferenceById(sessionID);
		String query = chatRequestDto.query();

		chatRepository.save(new ChatEntity(ChatType.USER, query, sessionEntity));

		return Flux.defer(() -> {
			StringBuilder full = new StringBuilder();

			Flux<ChatStreamEvent> tokens = ragService.getStreamResponse(query, sessionID)
					.doOnNext(full::append)
					.map(ChatStreamEvent::token);

			// runs only after tokens complete: saves the full reply, emits the same DTO as /chat
			Mono<ChatStreamEvent> done = Mono.fromSupplier(() -> ChatStreamEvent.done(
					chatMapper.entityToDto(chatRepository.save(
							new ChatEntity(ChatType.AI, full.toString(), sessionEntity)))));

			return tokens.concatWith(done);
		});
	}

}
