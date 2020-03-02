package com.chess.root.pieces;

import java.util.ArrayList;
import com.chess.model.Direction;
import com.chess.root.Board;
import com.chess.root.Field;
import com.chess.root.moves.Move;

public class KnightPiece extends Piece {
	
	private static String name = "knight";
	private static String notation = "N";
	private static Direction[] dirs = { Direction.BOTTOM_LEFT_DOWN, Direction.BOTTOM_LEFT_LEFT, Direction.BOTTOM_RIGHT_DOWN, Direction.BOTTOM_RIGHT_RIGHT, Direction.TOP_RIGHT_UP, Direction.TOP_RIGHT_RIGHT, Direction.TOP_LEFT_UP, Direction.TOP_LEFT_LEFT };
	private static final int[][] KNIGHT_UP = {
			{-50,-40,-30,-30,-30,-30,-40,-50}, 
			{-40,-20,0,0,0,0,-20,-40}, 
			{-30,0,10,15,15,10,0,-30}, 
			{-30,5,15,20,20,15,5,-30}, 
			{-30,0,15,20,20,15,0,-30}, 
			{-30,5,10,15,15,10,5,-30}, 
			{-40,-20,0,5,5,0,-20,-40}, 
			{-50,-40,-30,-30,-30,-30,-40,-50}
			}; 
	private static final int[][] KNIGHT_DOWN = {
			{-50,-40,-30,-30,-30,-30,-40,-50}, 
			{-40,-20,0,5,5,0,-20,-40}, 
			{-40,-20,0,0,0,0,-20,-40}, 
			{-30,5,10,15,15,10,5,-30}, 
			{-30,0,15,20,20,15,0,-30}, 
			{-30,5,15,20,20,15,5,-30},
			{-30,0,10,15,15,10,0,-30}, 
			{-50,-40,-30,-30,-30,-30,-40,-50}
			}; 

	public KnightPiece(Board board, Field field, boolean color) {
		super(board, field, color, name, notation, board.getPieceValue().knight(), !color ? KNIGHT_UP : KNIGHT_DOWN, false);
		fen = color ? "n" : "N";
	}
		
	// ---------------------------------- ABSTRACT METHODS ----------------------------------
	
	@Override
	public ArrayList<Move> getMoves() {
		ArrayList<Move> moves = new ArrayList<>();
		
		// find moves
		for (Direction direction : dirs) {
			int col = this.getField().getColumn() + direction.col();
		int row = this.getField().getRow() + direction.row();
			// check if next field coordinates are valid
		if (col >= 0 && col < 8 && row >= 0 && row < 8) {	
				Field next = board.getField(col, row);
				Piece victim = next.getPiece();
				// check if enemy piece is next
				if (victim != null) {
					if (victim.getColor() != this.getColor()) {
						moves.add(new Move(this, next, victim));
					}
				} else {
					moves.add(new Move(this, next, null));
				}
			}
		}
		return moves;
	}

}
