package com.manish.pragyan.common.exception;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityExceptionDelegate implements AuthenticationEntryPoint, AccessDeniedHandler {

	private final HandlerExceptionResolver resolver;

	public SecurityExceptionDelegate(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
		this.resolver = resolver;
	}

	@Override
	public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex)
			throws IOException {
		resolver.resolveException(req, res, null, ex);
	}

	@Override
	public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex)
			throws IOException {
		resolver.resolveException(req, res, null, ex);
	}
}