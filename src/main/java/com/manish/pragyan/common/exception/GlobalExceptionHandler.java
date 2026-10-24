package com.manish.pragyan.common.exception;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.manish.pragyan.common.response.ErrorResponse;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// ---------- Security (filter chain + method security) ----------

	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ErrorResponse> handleJwt(JwtException ex, HttpServletRequest req) {
		log.warn("JWT rejected for {} {}: {}", req.getMethod(), req.getRequestURI(), ex.getMessage());
		return unauthorized("Invalid or expired token.", req);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
		return unauthorized("Invalid credentials.", req);
	}

	@ExceptionHandler({ DisabledException.class, LockedException.class })
	public ResponseEntity<ErrorResponse> handleAccountState(AuthenticationException ex, HttpServletRequest req) {
		return error(HttpStatus.FORBIDDEN, "Account is disabled or locked.", req, null);
	}

	// Catches UsernameNotFoundException, InsufficientAuthenticationException, etc.
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex, HttpServletRequest req) {
		return unauthorized("Authentication is required to access this resource.", req);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
		// This advice swallows the exception, so ExceptionTranslationFilter never
		// turns "anonymous + denied" into 401. Do it here.
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || auth instanceof AnonymousAuthenticationToken) {
			return unauthorized("Authentication is required to access this resource.", req);
		}
		return error(HttpStatus.FORBIDDEN, "You do not have permission to access this resource.", req, null);
	}

	// ---------- Domain ----------

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
		return error(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex, HttpServletRequest req) {
		return error(HttpStatus.CONFLICT, ex.getMessage(), req, null);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
		log.warn("Data integrity violation for {} {}", req.getMethod(), req.getRequestURI(), ex);
		return error(HttpStatus.CONFLICT, "The request conflicts with existing data.", req, null);
	}

	// ---------- Validation ----------

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
			HttpServletRequest req) {
		List<Map<String, String>> errors = ex.getConstraintViolations().stream()
				.map(v -> Map.of("field", v.getPropertyPath().toString(), "message", v.getMessage())).toList();
		return error(HttpStatus.BAD_REQUEST, "One or more request parameters are invalid.", req, errors);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
				.map(e -> Map.of("field", e.getField(), "message",
						e.getDefaultMessage() == null ? "Invalid value" : e.getDefaultMessage()))
				.toList();
		ErrorResponse body = ErrorResponse.of(status.value(), title(status), "One or more fields are invalid.",
				path(request), errors);
		return handleExceptionInternal(ex, body, headers, status, request);
	}

	// All Spring MVC built-ins (405, 415, 404, missing params...) funnel through here.
	// Convert their ProblemDetail into ErrorResponse; pass our own through untouched.
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatusCode status, WebRequest request) {
		if (body instanceof ProblemDetail pd) {
			String message = pd.getDetail() != null ? pd.getDetail() : pd.getTitle();
			body = ErrorResponse.of(status.value(), title(status), message, path(request), null);
		}
		return super.handleExceptionInternal(ex, body, headers, status, request);
	}

	// ---------- Fallback ----------

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest req) {
		log.error("Unhandled exception for {} {}", req.getMethod(), req.getRequestURI(), ex);
		return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", req, null);
	}

	// ---------- helpers ----------

	private ResponseEntity<ErrorResponse> unauthorized(String message, HttpServletRequest req) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, "Bearer")
				.body(ErrorResponse.of(401, "Unauthorized", message, req.getRequestURI(), null));
	}

	private ResponseEntity<ErrorResponse> error(HttpStatus status, String message, HttpServletRequest req,
			List<Map<String, String>> errors) {
		return ResponseEntity.status(status).body(
				ErrorResponse.of(status.value(), status.getReasonPhrase(), message, req.getRequestURI(), errors));
	}

	private String title(HttpStatusCode status) {
		HttpStatus s = HttpStatus.resolve(status.value());
		return s != null ? s.getReasonPhrase() : "Error";
	}

	private String path(WebRequest request) {
		return ((ServletWebRequest) request).getRequest().getRequestURI();
	}
}