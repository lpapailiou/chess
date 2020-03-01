package com.chess.model;

public enum PieceValues {

	SUICIDE(-10000, -10000, -1000, -1000, -1000, 0), 
	RANDOM(200, 200, 200, 200, 200, 200), 
	SUPEREASY(500, 200, 200, 200, 200, 200), 
	VERYEASY(1000, 200, 200, 200, 200, 200),
	EASY(2000, 500, 200, 200, 200, 200),
	MEDIUM(3000, 700, 500, 500, 500, 200),
	MEDIUMER(5000, 1000, 500, 500, 500, 200),
	HARD(10000, 1500, 500, 500, 600, 200),
	HARDER(100000, 2000, 1000, 1000, 1200, 200),
	SUPERHARD(100000, 3000, 1500, 1500, 2000, 200),
	SUPERSUPERHARD(100000, 5000, 2000, 2000, 3000, 200); // default
	
	private final int king;
	private final int queen;
	private final int bishop;
	private final int knight;
	private final int rook;
	private final int pawn;

	PieceValues(int king, int queen, int bishop, int knight, int rook, int pawn) {
		this.king = king;
		this.queen = queen;
		this.bishop = bishop;
		this.knight = knight;
		this.rook = rook;
		this.pawn = pawn;
	}
	
	// ---------------------------------- GENERIC GETTERS ----------------------------------
	
	public int king() {
		return king;
	}
	
	public int queen() {
		return queen;
	}
	
	public int bishop() {
		return bishop;
	}
	
	public int knight() {
		return knight;
	}
	
	public int rook() {
		return rook;
	}
	
	public int pawn() {
		return pawn;
	}

}
