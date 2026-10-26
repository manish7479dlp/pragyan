package com.manish.pragyan.modules.rag.services;

import java.util.List;
import java.util.UUID;

import com.manish.pragyan.modules.rag.dtos.ChatRequestDto;
import com.manish.pragyan.modules.rag.dtos.ChatResponseDto;
import com.manish.pragyan.modules.rag.dtos.ChatStreamEvent;

import reactor.core.publisher.Flux;

public interface ChatService {

	public ChatResponseDto chat(UUID sessionID, ChatRequestDto chatRequestDto);
	
	public Flux<ChatStreamEvent> chatStream(UUID sessionID, ChatRequestDto chatRequestDto);
	
	public List<ChatResponseDto> getChatBySessionID(UUID seesionID);
}
