package com.chess.root.pieces;

import java.util.ArrayList;
import com.chess.model.Direction;
import com.chess.root.Board;
import com.chess.root.Field;
import com.chess.root.moves.Move;
import com.chess.root.moves.PassingMove;
import com.chess.root.moves.PawnRunMove;
import com.chess.root.moves.PromotionMove;

public class PawnPiece extends Piece {
	
	private static String name = "pawn";
	private static String notation = "";
	private int queenRating;
	private static Direction[] dirsMove = { Direction.TOP, Direction.BOTTOM };
	private static Direction[] dirsHit = { Direction.BOTTOM_LEFT, Direction.BOTTOM_RIGHT, Direction.TOP_RIGHT, Direction.TOP_LEFT };
	private static Direction[] dirsEnpassant = { Direction.LEFT, Direction.RIGHT };
	private static final int[][] PAWN_UP = {
			{200,200,200,200,200,200,200,200}, 
			{150,150,150,150,150,150,150,150}, 
			{10,10,20,30,30,20,10,10}, 
			{5,5,10,25,25,10,5,5}, 
			{0,0,0,20,20,0,0,0},
			{5,-5,-10,0,0,-10,-5,5},
			{5,10,10,-20,-20,10,10,5},
			{0,0,0,0,0,0,0,0}
			}; 
	private static final int[][] PAWN_DOWN = {
			{0,0,0,0,0,0,0,0}, 
			{5,10,10,-20,-20,10,10,5},
			{5,-5,-10,0,0,-10,-5,5},
			{0,0,0,20,20,0,0,0},
			{5,5,10,25,25,10,5,5},
			{10,10,20,30,30,20,10,10},
			{150,150,150,150,150,150,150,150},
			{200,200,200,200,200,200,200,200}
			}; 

	public PawnPiece(Board board, Field field, boolean color) {
		super(board, field, color, name, notation, board.getPieceValue().pawn(), !color ? PAWN_UP : PAWN_DOWN, false);
		queenRating = board.getPieceValue().queen();
		fen = color ? "p" : "P";
	}

	public boolean movesUp() {
		return !color;
	}
	
	public int getQueenRating() {
		return queenRating;
	}
	
	// ---------------------------------- ABSTRACT METHODS ----------------------------------
	
	@Override
	public ArrayList<Move> getMoves() {
		ArrayList<Move> moves = new ArrayList<>();
		
		// find moves
		for (Direction direction : dirsMove) {
			if (direction.up() == !color) {
				int col = this.getField().getColumn();
				int row = this.getField().getRow();
				for (int i = 0; i < 2; i++) {
					col += direction.col();
					row += direction.row();
					// check if next field coordinates are valid
					if (col >= 0 && col < 8 && row >= 0 && row < 8) {	
						Field next = board.getField(col, row);
						if (next.getPiece() == null) {
							if (Math.abs(this.getRow() - row) < 2) {
								if ((row == 0) || (row == 7)) {
									moves.add(new PromotionMove(this, this, next, null));
								} else {
									moves.add(new Move(this, next, null));
								}
							} else {
								moves.add(new PawnRunMove(this, next, null));
							}
						} else {
							break;
						}
						if ((!color && this.getField().getRow() != 6) || (color && this.getField().getRow() != 1)) {
							break;
						}
					}
				}
			}
		}
	
		// find hits
		for (Direction direction : dirsHit) {
			if (direction.up() == !color) {
				int col = this.getField().getColumn() + direction.col();
			int row = this.getField().getRow() + direction.row();
				// check if next field coordinates are valid
			if (col >= 0 && col < 8 && row >= 0 && row < 8) {	
					Field next = board.getField(col, row);
					Piece victim = next.getPiece();
					// check if enemy piece is next
					if (victim != null && victim.getColor() != this.getColor()) {
						if ((row == 0) || (row == 7)) {
							moves.add(new PromotionMove(this, this, next, victim));
						} else {
							moves.add(new Move(this, next, victim));
						}
					}
				}
			}
		}
		if (board.passingEnabled()) {
			
			Piece enPassant = board.getEnPassantPiece();
			if (enPassant != null) {
				for (Direction direction : dirsEnpassant) {
					int col = this.getColumn() + direction.col();
					int row = this.getRow();
					if (col >= 0 && col <= 7 && board.getField(col, row).getPiece() == enPassant) {
						Field targetField = null;
						if (this.getRow() == 3) {
							targetField = board.getField(enPassant.getColumn(), this.getRow()-1);
						} else if (this.getRow() == 4) {
							targetField = board.getField(enPassant.getColumn(), this.getRow()+1);
						}
						if (targetField != null) {
							moves.add(new PassingMove(this, targetField, enPassant));
						}
					}
				}
			}
		}
		return moves;
	}
	
	@Override
	public String getPgnNotation() {
		return field.getColNotation();
	}

}
