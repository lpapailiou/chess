package com.chess.model;

public enum Mode {
	
	MANUAL_VS_AI("You vs AI"), 
	MANUAL_ONLY("You vs your 'friend'"), 
	AI_ONLY("AI only");

	private final String value;

	Mode(String value) {
		this.value = value;
	}
	
	// ---------------------------------- GENERIC GETTER ----------------------------------

	public String get() {
		return value;
	}
}
