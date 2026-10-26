package com.manish.pragyan.modules.rag.services.impls;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.manish.pragyan.common.exception.ResourceNotFoundException;
import com.manish.pragyan.common.utils.AuthUser;
import com.manish.pragyan.modules.auth.repositories.AuthRepository;
import com.manish.pragyan.modules.rag.dtos.SessionResponseDto;
import com.manish.pragyan.modules.rag.dtos.SessionUpdateDto;
import com.manish.pragyan.modules.rag.entities.SessionEntity;
import com.manish.pragyan.modules.rag.mappers.SessionMapper;
import com.manish.pragyan.modules.rag.repositories.SessionRepository;
import com.manish.pragyan.modules.rag.services.SessionService;

@Service
public class SessionServiceImpl implements SessionService {
	private static final String DEFAULT_SESSION_NAME = "New Chat";
	
	private final SessionRepository sessionRepository;
	private final SessionMapper sessionMapper;
	private final AuthRepository authRepository;
	
	public SessionServiceImpl(SessionRepository sessionRepository,
			SessionMapper sessionMapper,
			AuthRepository authRepository) {
		this.sessionRepository = sessionRepository;
		this.sessionMapper = sessionMapper;
		this.authRepository = authRepository;
	}
	
	private String getEmail() {
		return AuthUser.get().getEmail();
	}
	
	private SessionEntity findOwned(UUID id) {
		return sessionRepository.findByIdAndUserEntity_Email(id, getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("Session Id: " + id + " not found"));	
	}

	@Override
	public SessionResponseDto create() {
		SessionEntity session = new SessionEntity(DEFAULT_SESSION_NAME,
				authRepository.getReferenceById(getEmail()), false);
		return sessionMapper.entityToDto(sessionRepository.save(session));
	}

	@Override
	public SessionResponseDto get(UUID id) {
		return sessionMapper.entityToDto(findOwned(id));
	}

	private static final Comparator<SessionEntity> PINNED_THEN_RECENT =
	        Comparator.comparing(SessionEntity::getIsPinned).reversed()
	                .thenComparing(Comparator.comparing(SessionEntity::getUpdatedAt).reversed());

	@Override
	public List<SessionResponseDto> get() {
	    return sessionRepository.findByUserEntity_Email(getEmail()).stream()
	            .sorted(PINNED_THEN_RECENT)
	            .map(sessionMapper::entityToDto)
	            .toList();
	}

	@Override
	public SessionResponseDto update(UUID id, SessionUpdateDto sessionUpdateDto) {
	    SessionEntity entity = findOwned(id);   
	    sessionMapper.updateSessionDtoToEntity(sessionUpdateDto, entity);
	    return sessionMapper.entityToDto(sessionRepository.save(entity));
	}

	@Override
	public void delete(UUID id) {
		sessionRepository.delete(findOwned(id));
	}

}
