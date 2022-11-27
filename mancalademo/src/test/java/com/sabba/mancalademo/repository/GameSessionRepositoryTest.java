package com.sabba.mancalademo.repository;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sabba.mancalademo.model.GameSession;
import com.sabba.mancalademo.model.GameStatusEnum;

@SpringBootTest()
class GameSessionRepositoryTest {
	
	@Autowired
	GameSessionRepository gameSessionRepository;

	@Test
	void testRepository() throws  Exception {
		gameSessionRepository.deleteAll();
		
		String sessionId = UUID.randomUUID().toString();
		String firstPlayer = UUID.randomUUID().toString();
		int numberOfSmallPitsPerSide = 6;
		int numberOfStonesPerPit = 6;
		
		GameSession gameSession = gameSessionRepository.save(new GameSession(sessionId, firstPlayer, numberOfSmallPitsPerSide, numberOfStonesPerPit));
		Assertions.assertNotNull(gameSession.getId());
		Assertions.assertNotNull(gameSessionRepository.findBySessionId(sessionId));
		
		GameSession fetchGameSessionById = gameSessionRepository.findBySessionId("this_id_does_not_exist");
        Assertions.assertNull(fetchGameSessionById);


        GameSession fetchGameSessionByGameState = gameSessionRepository.findByGameStatusEnum(GameStatusEnum.NEW);
        Assertions.assertNotNull(fetchGameSessionByGameState);
        
        fetchGameSessionByGameState = gameSessionRepository.findByGameStatusEnum(GameStatusEnum.INPROGRESS);
        Assertions.assertNull(fetchGameSessionByGameState);	
	}
}
