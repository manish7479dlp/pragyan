package com.manish.pragyan.modules.rag.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.manish.pragyan.common.response.ApiResponse;
import com.manish.pragyan.modules.rag.dtos.ChatRequestDto;
import com.manish.pragyan.modules.rag.dtos.ChatResponseDto;
import com.manish.pragyan.modules.rag.dtos.SessionResponseDto;
import com.manish.pragyan.modules.rag.dtos.SessionUpdateDto;
import com.manish.pragyan.modules.rag.services.ChatService;
import com.manish.pragyan.modules.rag.services.SessionService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/sessions")
public class SessionController {
	
	private final SessionService sessionService;
	private final ChatService chatService;
	
	public SessionController(SessionService sessionService,
			ChatService chatService) {
		this.sessionService = sessionService;
		this.chatService = chatService;
	}
	
	@PostMapping
	public ResponseEntity<ApiResponse<SessionResponseDto>> create() {
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(ApiResponse
						.success("Session created Successfully",
								sessionService.create()
						));
	}
	
	@GetMapping
	public ResponseEntity<ApiResponse<List<SessionResponseDto>>> getSessions() {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ApiResponse
						.success("Sessions fetched successfully",
								sessionService.get()
						));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<SessionResponseDto>> getSession(@PathVariable UUID id) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ApiResponse
						.success("Sessions fetch successfully",
								sessionService.get(id)
						));
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<ApiResponse<SessionResponseDto>> updateSession(@PathVariable UUID id,
			@Valid
			@RequestBody SessionUpdateDto sessionUpdateDto) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ApiResponse
						.success("Sessions updated successfully",
								sessionService.update(id, sessionUpdateDto)
						));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable UUID id) {
		sessionService.delete(id);
		return ResponseEntity.ok(ApiResponse.success("Session deleted successfully", null));
	}	
	
	
	// Chat related API
	
	@GetMapping("/{id}/chats")
	public ResponseEntity<ApiResponse<List<ChatResponseDto>>> getChatsBySessionID(@PathVariable(name = "id") UUID sessionID) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ApiResponse
						.success("Chats fetched successfully",
								chatService.getChatBySessionID(sessionID)
						));
	}
	
	@PostMapping("/{id}/chat") 
	public ResponseEntity<ApiResponse<ChatResponseDto>> chats(
			@PathVariable UUID id,
			
			@Valid
			@RequestBody ChatRequestDto chatRequestDto) {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(ApiResponse
						.success("Query process successfully",
								chatService.chat(id, chatRequestDto)
						));
	}
	
	@PostMapping(value = "/{id}/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ServerSentEvent<ApiResponse<?>>> chatStream(
			@PathVariable UUID id,

			@Valid
			@RequestBody ChatRequestDto chatRequestDto) {
		return chatService.chatStream(id, chatRequestDto)
				.map(e -> ServerSentEvent.<ApiResponse<?>>builder()
						.event(e.type())
						.data(ApiResponse.success(
								"done".equals(e.type()) ? "Query process successfully" : "chunk",
								e.data()))
						.build());
	}

}
