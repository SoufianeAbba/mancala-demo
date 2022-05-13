package com.sabba.mancalademo.model;

public class Sow {
	private String sessionId;
	private String player;
	private int pitIndex;
	private int rowIndex;
	
	public Sow(String sessionId, String player, int pitIndex, int rowIndex) {
		this.sessionId = sessionId;
		this.player = player;
		this.pitIndex = pitIndex;
		this.rowIndex = rowIndex;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public int getPitIndex() {
		return pitIndex;
	}

	public void setPitIndex(int pitIndex) {
		this.pitIndex = pitIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
}
