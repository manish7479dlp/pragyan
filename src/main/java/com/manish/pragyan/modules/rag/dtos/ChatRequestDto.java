package com.manish.pragyan.modules.rag.dtos;

import jakarta.validation.constraints.NotBlank;

public record ChatRequestDto(
		@NotBlank(message = "Query not be blank.")
		String query
		
		) {}

