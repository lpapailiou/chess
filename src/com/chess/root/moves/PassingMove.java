package com.chess.root.moves;

import java.util.List;
import com.chess.root.Board;
import com.chess.root.FenParser;
import com.chess.root.Field;
import com.chess.root.pieces.Piece;
import com.chess.root.pieces.RookPiece;

public class PassingMove extends Move {
	
	
	public PassingMove(Piece piece, Field field, Piece victim) {
		super(piece, field, victim);
	}

	
	@Override
	public void execute(Board board) {
		//field.restorePiece(piece);
		boolean countdownReset = false;
		if (victim != null) {
			victimField.removePiece(false);
			board.removePiece(victim);
			victimField.forceRemove();
			if (victim instanceof RookPiece) {
				((RookPiece) victim).kill();
			}
			countdownReset = true;
		}
				
		updateBoard(board, countdownReset);
		
		startField.removePiece(false);
		startField.forceRemove();
		
		field.setPiece(piece);
		//field.restorePiece(piece);
		fenBoard = FenParser.getBoard(board);
		
	}
	
	@Override
	protected void updateNotation() {
		super.updateNotation();
		if (this instanceof PassingMove) {
			notationSuffix += " e.p.";
		}
	}
	
	@Override
	public void undo(Board board) {
		super.undo(board);
		field.removePiece(false);
		field.forceRemove();
	}
	
	@Override
	public void undoSimulation(Board board, List<Piece> otherPieces) {
		startField.setPieceSilently(piece);
		piece.setFieldSilently(startField);
		board.setEnPassantPiece(enPassant);
		
		victimField.setPieceSilently(victim);
		victim.setFieldSilently(victimField);
		otherPieces.add(victim);
		field.removePieceSilently();
		
	}
	
	@Override
	public void flash() {
		super.flash();
		victimField.flash();
	}
	
	
	
}
