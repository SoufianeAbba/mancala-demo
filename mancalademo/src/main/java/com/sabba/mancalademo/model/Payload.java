package com.sabba.mancalademo.model;

public class Payload {
	private GameSession gameSession;
	private String errorMessage;
	private PayloadActionEnum payloadAction;
	
	public Payload() {
		
	}

	public Payload(GameSession gameSession, PayloadActionEnum payloadAction) {
		this.gameSession = gameSession;
		this.payloadAction = payloadAction;
	}

	public GameSession getGameSession() {
		return gameSession;
	}

	public void setGameSession(GameSession gameSession) {
		this.gameSession = gameSession;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public PayloadActionEnum getPayloadAction() {
		return payloadAction;
	}

	public void setPayloadAction(PayloadActionEnum payloadAction) {
		this.payloadAction = payloadAction;
	}
}
