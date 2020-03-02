package com.chess.root.pieces;

import java.util.ArrayList;
import com.chess.model.Direction;
import com.chess.root.Board;
import com.chess.root.Field;
import com.chess.root.moves.Move;

public class BishopPiece extends Piece {
	
	private static String name = "bishop";
	private static String notation = "B";
	private static Direction[] dirs = { Direction.BOTTOM_LEFT, Direction.BOTTOM_RIGHT, Direction.TOP_RIGHT, Direction.TOP_LEFT };
	private static final int[][] BISHOP_UP = {
			{-20,-10,-10,-10,-10,-10,-10,-20}, 
			{-10,0,0,0,0,0,0,-10}, 
			{-10,0,5,10,10,5,0,-10}, 
			{-10,5,5,10,10,5,5,-10}, 
			{-10,0,10,10,10,10,0,-10}, 
			{-10,10,10,10,10,10,10,-10}, 
			{-20,5,0,0,0,0,5,-20}, 
			{-20,-10,-10,-10,-10,-10,-10,-20}
			}; 
	private static final int[][] BISHOP_DOWN = {
			{-20,-10,-10,-10,-10,-10,-10,-20}, 
			{-20,5,0,0,0,0,5,-20}, 
			{-10,10,10,10,10,10,10,-10}, 
			{-10,0,10,10,10,10,0,-10}, 
			{-10,5,5,10,10,5,5,-10}, 
			{-10,0,5,10,10,5,0,-10}, 
			{-10,0,0,0,0,0,0,-10},
			{-20,-10,-10,-10,-10,-10,-10,-20}
			}; 

	public BishopPiece(Board board, Field field, boolean color) {
		super(board, field, color, name, notation, board.getPieceValue().bishop(), !color ? BISHOP_UP : BISHOP_DOWN, false);
		fen = color ? "b" : "B";
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
