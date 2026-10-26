package com.manish.pragyan.modules.rag.services;

import java.util.List;
import java.util.UUID;

import com.manish.pragyan.modules.rag.dtos.SessionResponseDto;
import com.manish.pragyan.modules.rag.dtos.SessionUpdateDto;

public interface SessionService {
	public SessionResponseDto create();
	
	public SessionResponseDto get(UUID id);
	
	public List<SessionResponseDto> get();
	
	public SessionResponseDto update(UUID id, SessionUpdateDto sessionUpdateDto);
	
	public void delete(UUID id);
	
}
