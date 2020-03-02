package com.chess.root.pieces;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.chess.model.Direction;
import com.chess.root.Board;
import com.chess.root.Field;
import com.chess.root.moves.CastlingMove;
import com.chess.root.moves.Move;

public class KingPiece extends Piece {
	
	private static String name = "king";
	private static String notation = "K";
	private static Direction[] dirs = { Direction.BOTTOM_LEFT, Direction.BOTTOM_RIGHT, Direction.TOP_RIGHT, Direction.TOP_LEFT, Direction.TOP, Direction.BOTTOM, Direction.LEFT, Direction.RIGHT };
	private boolean moved = false;
	private int movecounter = 0;
	private boolean init = true;
	private Piece rookQueenSide = null;
	private Piece rookKingSide = null;
	private static final int[][] KING_UP = {
			{-30,-40,-40,-50,-50,-40,-40,-30},
			{-30,-40,-40,-50,-50,-40,-40,-30},
			{-30,-40,-40,-50,-50,-40,-40,-30},
			{-30,-40,-40,-50,-50,-40,-40,-30},
			{-20,-30,-30,-40,-40,-30,-30,-20},
			{-10,-20,-20,-20,-20,-20,-20,-10},
			{20, 20,0, 0,0,0,20,20},
			{20,30,10,0,0,10,30,20}
			};
	private static final int[][] KING_DOWN = {
			{20,30,10,0,0,10,30,20},
			{20, 20,0, 0,0,0,20,20},
			{-10,-20,-20,-20,-20,-20,-20,-10},
			{-20,-30,-30,-40,-40,-30,-30,-20},
			{-30,-40,-40,-50,-50,-40,-40,-30},
			{-30,-40,-40,-50,-50,-40,-40,-30},
			{-30,-40,-40,-50,-50,-40,-40,-30},
			{-30,-40,-40,-50,-50,-40,-40,-30}
			};
	
	private static final int[][] KING_UP_END = {
			{-50,-40,-30,-20,-20,-30,-40,-50},
			{-30,-20,-10,0,0,-10,-20,-30},
			{-30,-10,20,30,30,20,-10,-30},
			{-30,-10,30,40,40,30,-10,-30},
			{-30,-10,30,40,40,30,-10,-30},
			{-30,-10,20,30,30,20,-10,-30},
			{-30,-30,0,0,0,0,-30,-30},
			{-50,-30,-30,-30,-30,-30,-30,-50}
			};
	private static final int[][] KING_DOWN_END = {
			{-50,-30,-30,-30,-30,-30,-30,-50},
			{-30,-30,0,0,0,0,-30,-30},
			{-30,-10,20,30,30,20,-10,-30},
			{-30,-10,30,40,40,30,-10,-30},
			{-30,-10,30,40,40,30,-10,-30},
			{-30,-10,20,30,30,20,-10,-30},
			{-30,-20,-10,0,0,-10,-20,-30},
			{-50,-40,-30,-20,-20,-30,-40,-50}
			};
	
	public KingPiece(Board board, Field field, boolean color) {
		super(board, field, color, name, notation, board.getPieceValue().king(), !color ? KING_UP : KING_DOWN, false);
		moved = color ? !field.getNotation().contentEquals("e8") : !field.getNotation().contentEquals("e1");
		fen = color ? "k" : "K";
	}
	
	private void checkInit() {
		if (init) {
			int row = this.isBlack() ? 0 : 7;
			if (board.getField(0,row).getPiece() != null && board.getField(0,row).getPiece().getColor() == this.color) {
				rookQueenSide = board.getField(0,row).getPiece();
			}
			if (board.getField(7,row).getPiece() != null && board.getField(7,row).getPiece().getColor() == this.color) {
				rookKingSide = board.getField(7,row).getPiece();
			}
		}
		init = false;
	}
	
	@Override
	public void setEndTable(boolean end) {
		if (end) {
			table = color ? KING_DOWN_END : KING_UP_END;
		} else {
			table = color ? KING_DOWN : KING_UP;
		}
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
		
	@Override
	public String getCastlingFen() {
		checkInit();
		String s = "";
		if (!moved) {
			if (rookKingSide != null && !rookKingSide.wasMoved()) {
				s = "K";
			}
			if (rookQueenSide != null && !rookQueenSide.wasMoved()) {
				s += "Q";
			}
		}
		
		if (this.isBlack()) {
			s = s.toLowerCase();
		}
		return s;
	}
	
	@Override
	public void initializeFenCastling(String cas) {
		checkInit();
		if (this.isBlack()) {
			if (!cas.contains("k") && rookKingSide != null) {
				rookKingSide.moved();
			}
			if (!cas.contains("q") && rookQueenSide != null) {
				rookQueenSide.moved();
			}
		} else {
			if (!cas.contains("K") && rookKingSide != null) {
				rookKingSide.moved();
			}
			if (!cas.contains("Q") && rookQueenSide != null) {
				rookQueenSide.moved();
			}
		}
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

	
	public List<Move> getCastlingMoves() {
		checkInit();
		ArrayList<Move> moves = new ArrayList<>();
		// check for castling moves
		if (!this.moved) {
			if (rookKingSide != null && !rookKingSide.wasMoved() && !rookKingSide.isDead()) {
				Optional.ofNullable(checkCastling(rookKingSide)).ifPresent(moves::add);
			}
			if (rookQueenSide != null && !rookQueenSide.wasMoved() && !rookQueenSide.isDead()) {
				Optional.ofNullable(checkCastling(rookQueenSide)).ifPresent(moves::add);
			}
		}
		if (!moves.isEmpty()) {
			return moves;
		}
		return null;
	}
	
	private Move checkCastling(Piece rook) {
		boolean movesLeft = false;
		List<Piece> otherPieces = board.getPieces(!this.isBlack());
		int diff = this.getColumn() - rook.getColumn();
		if (this.getColumn() - rook.getColumn() > 0) {
			movesLeft = true;
		}

		int col = this.getColumn();
		int row = this.getRow();	
		for (int i = 0; i < 3; i++) {
			// check if field is free & != start field
			boolean endangered = false;
			Field next = null;
			if (i != 0) {	
				next = board.getField(col, row);
				if (next.getPiece() != null) {
					return null;
				}
				Field startField = this.getField();
				this.getField().removePieceSilently(); 
				this.setFieldSilently(next);
				next.setPieceSilently(this);
				if (board.isPieceEndangered(this, otherPieces)) {
						endangered = true;
					}
				startField.setPieceSilently(this);
				this.setFieldSilently(startField);
				next.removePieceSilently();
				
			} else {
				if (board.isPieceEndangered(this, otherPieces)) {
					endangered = true;
				}
			}
			if (endangered) {
				return null;
			}
			if (movesLeft) {
				col--;
			} else {
				col++;
			}
			if (i == 2 && (Math.abs(diff) == 4)) {
				Field test = board.getField(col, this.getRow());
				if (test.getPiece() != null) {
					return null;
				}
			}
			
		}
		int rookVal1 = movesLeft ? -2 : 2;
		int rookVal2 = movesLeft ? -1 : 1;

		return new CastlingMove(this, rook, board.getField(this.getColumn()+rookVal1, this.getRow()), board.getField(this.getColumn()+rookVal2, this.getRow()));
	}
}
