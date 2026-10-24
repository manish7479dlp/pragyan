package com.manish.pragyan.modules.auth.dtos;

import com.manish.pragyan.modules.auth.entities.Provider;
import com.manish.pragyan.modules.auth.entities.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterResponseDto(
		@NotBlank(message = "Name is required")
        @Size(min = 3, max = 16, message = "Name must be between 3 and 16 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters")
        String password,
		
		@NotNull(message = "Role is required")
		Role role,
		
	    
		Provider provider) {}
