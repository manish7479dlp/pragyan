package com.manish.pragyan.modules.auth.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.manish.pragyan.modules.auth.dtos.RegisterResponseDto;
import com.manish.pragyan.modules.auth.entities.CustomUserDetails;
import com.manish.pragyan.modules.auth.services.AuthService;
import com.manish.pragyan.modules.auth.services.CustomUserDetailsService;
import com.manish.pragyan.modules.auth.services.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

	private static final Logger logger = LoggerFactory.getLogger(Oauth2SuccessHandler.class);

	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private final AuthService authService;
	private final JwtService jwtService;
	private final CustomUserDetailsService customUserDetailsService;

	public Oauth2SuccessHandler(
			@Lazy AuthService authService,
			JwtService jwtService,
			CustomUserDetailsService customUserDetailsService) {
		this.authService = authService;
		this.jwtService = jwtService;
		this.customUserDetailsService = customUserDetailsService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {

		OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
		String registrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		RegisterResponseDto registerResponseDto = authService.oAuth2Login(oAuth2User, registrationId);

		String email = registerResponseDto.email();
		CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);
		String token = jwtService.generateToken(email, customUserDetails);

		String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:5432/oauth2/redirect")
				.queryParam("token", token)
				.build()
				.toUriString();

		if (response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to redirect to {}", targetUrl);
			return;
		}

		redirectStrategy.sendRedirect(request, response, targetUrl);
	}
}