package com.sabba.mancalademo.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class GameSessionTest {

	@Test
	void testNewGame() {
		String sessionId = UUID.randomUUID().toString();
		String firstPlayer = UUID.randomUUID().toString();
		int numberOfSmallPitsPerSide = 12;
		int numberOfStonesPerPit = 7;
		GameSession gameSession = new GameSession(sessionId, firstPlayer, numberOfSmallPitsPerSide, numberOfStonesPerPit);
		
		assertNotNull(gameSession);
		assertEquals(GameStatusEnum.NEW, gameSession.getGameStatusEnum());
		
		int[] testPits = new int[numberOfSmallPitsPerSide + 1];
		Arrays.fill(testPits, 0, numberOfSmallPitsPerSide, numberOfStonesPerPit);
		assertArrayEquals(testPits, gameSession.getMancalaPits()[0]);
		
		gameSession = new GameSession(sessionId, firstPlayer);
		assertNotNull(gameSession);
		assertEquals(GameStatusEnum.NEW, gameSession.getGameStatusEnum());
		
		testPits = new int[7];
		Arrays.fill(testPits, 0, 6, 6);
		assertArrayEquals(testPits, gameSession.getMancalaPits()[0]);
	}
	
	@Test
	void TestAFewSows() {
		String sessionId = UUID.randomUUID().toString();
		String firstPlayer = UUID.randomUUID().toString();
		String secondPlayer = UUID.randomUUID().toString();
		int numberOfSmallPitsPerSide = 6;
		int numberOfStonesPerPit = 6;
		GameSession gameSession = new GameSession(sessionId, firstPlayer, numberOfSmallPitsPerSide, numberOfStonesPerPit);
		
		gameSession.setSecondPlayer(secondPlayer);
		gameSession.setGameStatusEnum(GameStatusEnum.INPROGRESS);
		
		Sow sow = new Sow(sessionId, firstPlayer, 3, 0);
		gameSession.sow(sow);	
		int[] player2Pits = new int[]{7, 7, 7, 6, 6, 6, 0};
		int[] player1Pits = new int[]{6, 6, 6, 0, 7, 7, 1};
		assertArrayEquals(player2Pits, gameSession.getMancalaPits()[1]);
		assertArrayEquals(player1Pits, gameSession.getMancalaPits()[0]);
		
		sow.setPlayer(secondPlayer);
		sow.setPitIndex(0);
		sow.setRowIndex(1);
		gameSession.sow(sow);
		player2Pits = new int[]{0, 8, 8, 7, 7, 7, 1};
		player1Pits = new int[]{7, 6, 6, 0, 7, 7, 1};
		assertArrayEquals(player2Pits, gameSession.getMancalaPits()[1]);
		assertArrayEquals(player1Pits, gameSession.getMancalaPits()[0]);
		
		sow.setPlayer(firstPlayer);
		sow.setPitIndex(0);
		sow.setRowIndex(0);
		gameSession.sow(sow);
		player2Pits = new int[]{1, 8, 8, 7, 7, 7, 1};
		player1Pits = new int[]{0, 7, 7, 1, 8, 8, 2};
		assertArrayEquals(player2Pits, gameSession.getMancalaPits()[1]);
		assertArrayEquals(player1Pits, gameSession.getMancalaPits()[0]);
		
		sow.setPlayer(secondPlayer);
		sow.setPitIndex(3);
		sow.setRowIndex(1);
		gameSession.sow(sow);
		player2Pits = new int[]{1, 8, 8, 0, 8, 8, 2};
		player1Pits = new int[]{1, 8, 8, 2, 8, 8, 2};
		assertArrayEquals(player2Pits, gameSession.getMancalaPits()[1]);
		assertArrayEquals(player1Pits, gameSession.getMancalaPits()[0]);		
	}
	
	@Test
	void TestAStealingSow() {
		String sessionId = UUID.randomUUID().toString();
		String firstPlayer = UUID.randomUUID().toString();
		String secondPlayer = UUID.randomUUID().toString();
		int numberOfSmallPitsPerSide = 6;
		int numberOfStonesPerPit = 6;
		GameSession gameSession = new GameSession(sessionId, firstPlayer, numberOfSmallPitsPerSide, numberOfStonesPerPit);
		
		gameSession.setSecondPlayer(secondPlayer);
		gameSession.setGameStatusEnum(GameStatusEnum.INPROGRESS);
			
		int[] player2Pits = new int[]{6, 6, 6, 6, 6, 6, 0};
		int[] player1Pits = new int[]{6, 6, 1, 0, 6, 6, 0};
		
		int[][] pits = new int[2][7];
		pits[0] = Arrays.copyOf(player1Pits, 7);
		pits[1] = Arrays.copyOf(player2Pits, 7); 
		gameSession.setMancalaPits(pits);
		
		Sow sow = new Sow(sessionId, firstPlayer, 2, 0);
		gameSession.sow(sow);
		
		player2Pits = new int[]{6, 6, 0, 6, 6, 6, 0};
		player1Pits = new int[]{6, 6, 0, 0, 6, 6, 7};
		
		assertArrayEquals(player2Pits, gameSession.getMancalaPits()[1]);
		assertArrayEquals(player1Pits, gameSession.getMancalaPits()[0]);
	}
	
	@Test
	void TestAGameEndingSow() {
		String sessionId = UUID.randomUUID().toString();
		String firstPlayer = UUID.randomUUID().toString();
		String secondPlayer = UUID.randomUUID().toString();
		int numberOfSmallPitsPerSide = 6;
		int numberOfStonesPerPit = 6;
		GameSession gameSession = new GameSession(sessionId, firstPlayer, numberOfSmallPitsPerSide, numberOfStonesPerPit);
		
		gameSession.setSecondPlayer(secondPlayer);
		gameSession.setGameStatusEnum(GameStatusEnum.INPROGRESS);
			
		int[] player2Pits = new int[]{0, 5, 0, 3, 2, 0, 2};
		int[] player1Pits = new int[]{0, 0, 0, 0, 0, 1, 5};
		
		int[][] pits = new int[2][7];
		pits[0] = Arrays.copyOf(player1Pits, 7);
		pits[1] = Arrays.copyOf(player2Pits, 7); 
		gameSession.setMancalaPits(pits);
		
		Sow sow = new Sow(sessionId, firstPlayer, 5, 0);
		gameSession.sow(sow);
		
		player2Pits = new int[]{0, 0, 0, 0, 0, 0, 12};
		player1Pits = new int[]{0, 0, 0, 0, 0, 0, 6};
		
		assertArrayEquals(player2Pits, gameSession.getMancalaPits()[1]);
		assertArrayEquals(player1Pits, gameSession.getMancalaPits()[0]);
		assertEquals(GameStatusEnum.FINISHED, gameSession.getGameStatusEnum());
	}
}
