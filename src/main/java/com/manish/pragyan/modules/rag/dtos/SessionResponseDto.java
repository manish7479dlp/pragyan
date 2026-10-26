package com.manish.pragyan.modules.rag.dtos;

import java.util.UUID;

public record SessionResponseDto(
		UUID id,
		
		String name,
		
		Boolean isPinned) {}
