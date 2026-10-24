package com.manish.pragyan.modules.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginResponseDto(
		@NotNull
		RegisterResponseDto user,
		
		@NotBlank
		String token){}
