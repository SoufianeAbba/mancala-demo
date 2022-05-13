package com.sabba.mancalademo.model;

import java.util.Arrays;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "gamesession")
public class GameSession {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	@Column(name = "sessionid")
	private String sessionId;
	@Column(name = "firstplayer")
	private String firstPlayer;
	@Column(name = "secondplayer")
	private String secondPlayer;
	@Column(name = "nextup")
	private String nextUp;
	@Column(name = "gamestatus")
	private GameStatusEnum gameStatusEnum;
	@Column(name = "mancalapits")
	private int[][] mancalaPits;
	
	public GameSession() {}
	
	/**
	 * Create a new game session with the first player and 
	 * a fixed mancala pits setup of 6 small pits per player with 6 stone in each.
	 * 
	 * @param sessionId A generic value to tag the specific game session.
	 * @param firstPlayer The first player that connects to a new game session.
	 */
	public GameSession(String sessionId, String firstPlayer) {
		this.sessionId = sessionId;
		this.firstPlayer = firstPlayer;
		this.nextUp = firstPlayer;
		this.gameStatusEnum = GameStatusEnum.NEW;
		this.mancalaPits = new int[2][7];
		Arrays.fill(this.mancalaPits[0], 0, 6, 6);
		Arrays.fill(this.mancalaPits[1], 0, 6, 6);
	}
	
	/**
	 * Create a new game session with the first player and setting up the mancala pits 
	 * with the given number of small pits per side and the given number of stones per pit.
	 * 
	 * @param sessionId A generic value to tag the specific game session.
	 * @param firstPlayer The first player that connects to a new game session.
	 * @param numberOfSmallPitsPerSide The first player that connects to a new game session.
	 * @param numberOfStonesPerPit The first player that connects to a new game session.
	 */
	public GameSession(String sessionId, String firstPlayer, int numberOfSmallPitsPerSide, int numberOfStonesPerPit) {
		this.sessionId = sessionId;
		this.firstPlayer = firstPlayer;
		this.nextUp = firstPlayer;
		this.gameStatusEnum = GameStatusEnum.NEW;
		this.mancalaPits = new int[2][numberOfSmallPitsPerSide + 1];
		Arrays.fill(this.mancalaPits[0], 0, numberOfSmallPitsPerSide, numberOfStonesPerPit);
		Arrays.fill(this.mancalaPits[1], 0, numberOfSmallPitsPerSide, numberOfStonesPerPit);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getFirstPlayer() {
		return firstPlayer;
	}

	public void setFirstPlayer(String firstPlayer) {
		this.firstPlayer = firstPlayer;
	}
	
	public String getSecondPlayer() {
		return secondPlayer;
	}

	public void setSecondPlayer(String secondPlayer) {
		this.secondPlayer = secondPlayer;
	}

	public String getNextUp() {
		return nextUp;
	}

	public void setNextUp(String nextUp) {
		this.nextUp = nextUp;
	}

	public int[][] getMancalaPits() {
		return mancalaPits;
	}

	public void setMancalaPits(int[][] mancalaPits) {
		this.mancalaPits = mancalaPits;
	}

	public GameStatusEnum getGameStatusEnum() {
		return gameStatusEnum;
	}

	public void setGameStatusEnum(GameStatusEnum gameStatusEnum) {
		this.gameStatusEnum = gameStatusEnum;
	}

	private void switchNextUp() {
		nextUp = (Objects.equals(firstPlayer, nextUp)) ? secondPlayer : firstPlayer;
	}

	private void captureStones(int playerPitSide, int bigPitIndex, int lastPitSideToSow, int lastPitIndex) {
		int stonesToAddToOwnBigPit = 0;
		int smallPitsPerSide = mancalaPits[playerPitSide].length - 2;

		stonesToAddToOwnBigPit += mancalaPits[lastPitSideToSow][lastPitIndex];
		mancalaPits[lastPitSideToSow][lastPitIndex] = 0;

		lastPitSideToSow = (lastPitSideToSow == 0) ? 1 : 0;
		stonesToAddToOwnBigPit += mancalaPits[lastPitSideToSow][Math.abs(lastPitIndex - smallPitsPerSide)];
		mancalaPits[lastPitSideToSow][Math.abs(lastPitIndex - smallPitsPerSide)] = 0;

		mancalaPits[playerPitSide][bigPitIndex] += stonesToAddToOwnBigPit;
	}

	public boolean CheckIfPlayerSelectedOwnPit(Sow sow) {
		int playerPitSide = 0;

		if (Objects.equals(firstPlayer, sow.getPlayer())) {
			playerPitSide = 0;
		}
		
		if (Objects.equals(secondPlayer, sow.getPlayer())) {
			playerPitSide = 1;
		}

		return (playerPitSide == sow.getRowIndex());
	}

	public boolean CheckIfPlayerSelectedAnEmptyPit(Sow sow) {
		return (mancalaPits[sow.getRowIndex()][sow.getPitIndex()] == 0);
	}

	private void checkIfGameIsFinished() {
		int playerPitSideToGather = 0;
		int numberOfPitsPerSide = mancalaPits[0].length;
		int numberOfStonesPlayer1 = 0;
		int numberOfStonesPlayer2 = 0;		

		for(int i = 0; i < numberOfPitsPerSide - 1; i++) {
			numberOfStonesPlayer1 += mancalaPits[0][i];
			numberOfStonesPlayer2 += mancalaPits[1][i];
		}

		if (numberOfStonesPlayer1 == 0 || numberOfStonesPlayer2 == 0) {
			playerPitSideToGather = (numberOfStonesPlayer1 == 0) ? 1 : 0;
			Arrays.fill(this.mancalaPits[playerPitSideToGather], 0, numberOfPitsPerSide - 1, 0);
			mancalaPits[playerPitSideToGather][numberOfPitsPerSide - 1] += (playerPitSideToGather == 0) ? numberOfStonesPlayer1 : numberOfStonesPlayer2;

			setGameStatusEnum(GameStatusEnum.FINISHED);
		}
	}

	public void sow(Sow sow) {
		int playerPitSide = 0;

		if (Objects.equals(firstPlayer, sow.getPlayer())) {
			playerPitSide = 0;
		}
		
		if (Objects.equals(secondPlayer, sow.getPlayer())) {
			playerPitSide = 1;
		}

		// Sow stones, skip opponent big pits.
		int currentPitSideToSow = sow.getRowIndex();
		int currentPitIndex = sow.getPitIndex();
		int bigPitIndex = mancalaPits[currentPitSideToSow].length - 1;
		int stonesToSow = mancalaPits[currentPitSideToSow][currentPitIndex] + 1;
		mancalaPits[currentPitSideToSow][currentPitIndex] = -1;	
		int numberOfPitsPerSide = mancalaPits[currentPitSideToSow].length;	

		while (stonesToSow > 0) {
			for(int i = currentPitIndex; i < numberOfPitsPerSide; i++) {
				// Skip big pit of opponent.
				if ((i ==  bigPitIndex && currentPitSideToSow != playerPitSide) || stonesToSow == 0) {
					break;
				}
				else {
					currentPitIndex = i;
				}
				
				mancalaPits[currentPitSideToSow][i]++;
				stonesToSow--;
			}
			
			if (stonesToSow > 0) {
				currentPitSideToSow = (currentPitSideToSow == 0) ? 1 : 0;
				currentPitIndex = 0;
			}
		}

		// Always switch up player name first.
		switchNextUp();
		
		// Check if last pit is owned by player. Some rules apply only when ending in player owned pit.
		if (currentPitSideToSow == playerPitSide) {
			// If the last pit is the players big pit, switch back up. Player can move again.
			if (currentPitIndex == bigPitIndex) switchNextUp();
			// Check if ended in own empty small pit. Player can capture stones.
			if (mancalaPits[currentPitSideToSow][currentPitIndex] == 1) captureStones(playerPitSide, bigPitIndex, currentPitSideToSow, currentPitIndex);
		}

		// Check if own pits are empty. If so move stones of remaining side to opponent and end game.
		checkIfGameIsFinished();
	}
}
