package com.chess.root.pieces;

import java.util.ArrayList;
import com.chess.model.Direction;
import com.chess.root.Board;
import com.chess.root.Field;
import com.chess.root.moves.Move;

public class RookPiece extends Piece {
	
	private static String name = "rook";
	private static String notation = "R";
	private static Direction[] dirs = { Direction.TOP, Direction.BOTTOM, Direction.LEFT, Direction.RIGHT };
	private static final int[][] ROOK_UP = {
			{0,0,0,0,0,0,0,0},
			{5,10,10,10,10,10,10,5},
			{-5,0,0,0,0,0,0,-5},
			{-5,0,0,0,0,0,0,-5},
			{-5,0,0, 0,0,0,0,-5},
			{-5,0,0,0,0,0,0,-5},
			{-5,0,0,0,0,0,0,-5},
			{0,0,0,5,5,0,0,0}
			};
	private static final int[][] ROOK_DOWN = {
			{0,0,0,5,5,0,0,0},
			{-5,0,0,0,0,0,0,-5},
			{-5,0,0,0,0,0,0,-5},
			{-5,0,0,0,0,0,0,-5},
			{-5,0,0,0,0,0,0,-5},
			{-5,0,0,0,0,0,0,-5},
			{5,10,10,10,10,10,10,5},
			{0,0,0,0,0,0,0,0}
			};
	private boolean moved = false;
	private int movecounter = 0;
	private boolean dead = false;
	
	public RookPiece(Board board, Field field, boolean color) {
		super(board, field, color, name, notation, board.getPieceValue().rook(), !color ? ROOK_UP : ROOK_DOWN, false);
		moved = color ? !(field.getNotation().contentEquals("a8") || field.getNotation().contentEquals("h8")) : !(field.getNotation().contentEquals("a1") || field.getNotation().contentEquals("h1"));
		fen = color ? "r" : "R";
	}
	
	@Override
	public boolean wasMoved() {
		return moved;
	}
	
	@Override
	public void moved() {
		movecounter++;
		moved = true;
	}
	
	@Override
	public void unmove() {
		movecounter--;
		if (movecounter == 0) {
			moved = false;
		}
	}
	
	public void kill() {
		dead = true;
	}
	
	public void revive() {
		dead = false;
	}
	
	@Override
	public boolean isDead() {
		return dead;
	}
	
	// ---------------------------------- ABSTRACT METHODS ----------------------------------
	
	@Override
	public ArrayList<Move> getMoves() {
		ArrayList<Move> moves = new ArrayList<>();
		
		// find moves
		for (Direction direction : dirs) {
			int col = this.getField().getColumn();
			int row = this.getField().getRow();
			for (int i = 0; i < 8; i++) {
				col += direction.col();
				row += direction.row();
				// check if next field coordinates are valid
				if (col >= 0 && col < 8 && row >= 0 && row < 8) {				
					Field next = board.getField(col, row);
					Piece victim = next.getPiece();
					// check if enemy piece is next
					if (victim != null) {
						if (victim.getColor() != this.getColor()) {
								moves.add(new Move(this, next, victim));
						}
						break;
					} else {
						moves.add(new Move(this, next, null));
					}
				}
			}
		}
		return moves;
	}


}
