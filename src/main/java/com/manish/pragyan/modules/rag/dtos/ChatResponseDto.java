package com.manish.pragyan.modules.rag.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.manish.pragyan.modules.rag.entities.ChatType;

public record ChatResponseDto(
		UUID id,
		
		String content,
		
		ChatType type,
		
		LocalDateTime createdAt
		) {}
