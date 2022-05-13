package com.sabba.mancalademo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sabba.mancalademo.model.GameSession;
import com.sabba.mancalademo.model.GameStatusEnum;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
	GameSession findBySessionId(String sessionId);
	GameSession findByGameStatusEnum(GameStatusEnum gameStatusEnum);
}
