package com.manish.pragyan.modules.rag.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SessionUpdateDto(
		@NotBlank(message = "Session name not be empty")
		@Size(min=2, max=100)
		String name,
		
		@NotNull(message = "isPinned must have boolean value")
		Boolean isPinned
		
		) {}
