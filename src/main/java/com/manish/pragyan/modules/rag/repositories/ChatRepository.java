package com.manish.pragyan.modules.rag.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manish.pragyan.modules.rag.entities.ChatEntity;

public interface ChatRepository extends JpaRepository<ChatEntity, UUID> {
	List<ChatEntity> findBySessionEntity_IdOrderByCreatedAtDesc(UUID sessionID);
}