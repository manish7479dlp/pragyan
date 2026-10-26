package com.manish.pragyan.modules.rag.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manish.pragyan.modules.rag.entities.SessionEntity;

public interface SessionRepository extends JpaRepository<SessionEntity, UUID> {
	public Optional<SessionEntity> findByIdAndUserEntity_Email(UUID id, String email);

	public List<SessionEntity> findByUserEntity_Email(String email);
}
