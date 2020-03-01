package com.chess.model;

public enum Direction {

	BOTTOM_LEFT(-1, 1, false), 
	BOTTOM_RIGHT(1, 1, false), 
	TOP_RIGHT(1, -1, true), 
	TOP_LEFT(-1, -1, true),
	TOP(0, -1, true),
	BOTTOM(0, 1, false),
	LEFT(-1, 0, false),
	RIGHT(1, 0, false),
	BOTTOM_LEFT_DOWN(-1, 2, false),
	BOTTOM_LEFT_LEFT(-2, 1, false), 
	BOTTOM_RIGHT_DOWN(1, 2, false), 
	BOTTOM_RIGHT_RIGHT(2, 1, false), 
	TOP_RIGHT_UP(1, -2, true), 
	TOP_RIGHT_RIGHT(2, -1, true), 
	TOP_LEFT_UP(-1, -2, true),
	TOP_LEFT_LEFT(-2, -1, true);
	
	private final int col;
	private final int row;
	private final boolean up;

	Direction(int col, int row, boolean up) {
		this.col = col;
		this.row = row;
		this.up = up;
	}
		
	// ---------------------------------- GENERIC GETTERS ----------------------------------

	public int col() {
		return col;
	}

	public int row() {
		return row;
	}

	public boolean up() {
		return up;
	}
		
}
