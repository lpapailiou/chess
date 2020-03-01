package com.chess.root.moves;

import java.util.List;
import com.chess.root.Board;
import com.chess.root.Field;
import com.chess.root.pieces.Piece;

public class PawnRunMove extends Move {
	
	public PawnRunMove(Piece piece, Field field, Piece victim) {
		super(piece, field, victim);
	}
	
	@Override
	protected void updateBoard(Board board, boolean countdownReset) {
		super.updateBoard(board, countdownReset);
		board.setEnPassantPiece(piece);
	}
	
	@Override
	public void executeSimulation(Board board, List<Piece> otherPieces) {
		super.executeSimulation(board, otherPieces);
		board.setEnPassantPiece(piece);
	}
}
