package com.sabba.mancalademo.model;

public record Payload (GameSession gameSession, String errorMessage, PayloadActionEnum payloadAction) {
	public Payload() {
		this(null, "", null);

	}
}
