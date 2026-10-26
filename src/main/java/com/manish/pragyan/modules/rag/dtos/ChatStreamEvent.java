package com.manish.pragyan.modules.rag.dtos;

public record ChatStreamEvent(String type, Object data) {
	public static ChatStreamEvent token(String t) { return new ChatStreamEvent("token", t); }
	public static ChatStreamEvent done(ChatResponseDto d) { return new ChatStreamEvent("done", d); }
}
