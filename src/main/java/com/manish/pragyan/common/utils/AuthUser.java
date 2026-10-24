package com.manish.pragyan.common.utils;

import org.springframework.security.core.context.SecurityContextHolder;

import com.manish.pragyan.modules.auth.entities.CustomUserDetails;

public class AuthUser {
	public static CustomUserDetails get() {
		return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
}
