package com.chess.root.moves;

import com.chess.root.Board;
import com.chess.root.Field;
import com.chess.root.pieces.Piece;
import com.chess.root.pieces.RookPiece;

public class CastlingMove extends Move {

	private Piece rook;
	private Field rookField;
	private Field rookStartField;

	public CastlingMove(Piece king, Piece rook, Field kingField, Field rookField) {
		super(king, kingField, null);
		this.rook = rook;
		this.rookField = rookField;
		this.rookStartField = rook.getField();
		if (Math.abs(king.getField().getColumn() - rook.getField().getColumn()) > 3) {
			notation = "O-O-O";
		} else {
			notation = "O-O";
		}
		updateRating(king);
	}
	
	@Override
	public void execute(Board board) {
		rook.getField().removePiece(false);
		rook.getField().forceRemove();
		rookField.setRookPiece(rook);
		((RookPiece) rook).moved();
		super.execute(board);
	}
	
	@Override
	public void undo(Board board) {
		rookField.removePiece(false);
		rookField.forceRemove();
		rookStartField.setRookPiece(rook);
		((RookPiece) rook).unmove();
		super.undo(board);
	}
	
	@Override
	public void flash() {
		super.flash();
		rookStartField.flash();
		rookField.flash();
	}
	
	@Override
	public String getNotation() {
		return notation;
	}
	
	@Override
	public String getPgnNotation() {
		return notation;
	}
	
	// ---------------------------------- PRE-CALCULATE BASE RATING ----------------------------------

	private void updateRating(Piece king) {
		super.rating += king.getRating();
	}
	
	public Piece getRook() {
		return rook;
	}
	
	public Field getRookField() {
		return rookField;
	}
	
	public Field getRookStartField() {
		return rookStartField;
	}

}
