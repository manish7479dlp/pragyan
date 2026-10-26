package com.manish.pragyan.modules.rag.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "chats")
public class ChatEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	UUID id;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	ChatType type;
	
	@Column(nullable = false, columnDefinition = "TEXT")
	String content;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "session_id", updatable = false, nullable = false)
	SessionEntity sessionEntity;
	
	@Column(updatable = false, nullable = false)
	LocalDateTime createdAt;
	
	@Column(nullable = false)
	LocalDateTime updatedAt;
	
	@PrePersist
	void prePersist() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
	}
	
	@PreUpdate
	void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
	
	public ChatEntity() {
		
	}

	public ChatEntity(ChatType type, String content, SessionEntity sessionEntity) {
		super();
		this.type = type;
		this.content = content;
		this.sessionEntity = sessionEntity;
	}

	public UUID getId() {
		return id;
	}

	public ChatType getType() {
		return type;
	}

	public void setType(ChatType type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public SessionEntity getSessionEntity() {
		return sessionEntity;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

}
