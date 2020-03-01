package com.chess.root.moves;

import java.util.List;
import com.chess.root.Board;
import com.chess.root.FenParser;
import com.chess.root.Field;
import com.chess.root.pieces.Piece;
import com.chess.root.pieces.QueenPiece;
import com.chess.root.pieces.RookPiece;

public class PromotionMove extends Move {
	
	private Piece pawn;
	private Piece queen;
		
		public PromotionMove(Piece piece, Piece queen, Field field, Piece victim) {
			super(queen, field, victim);
			this.pawn = piece;
			rating = queen.getRating() * 2;
		}
		
		@Override
		public Piece getPawn() {
			return pawn;
		}
		
		private void setQueenReally(Piece q) {
			this.piece = q;
			queen = q;
			q.getField().setPiece(q);
		}
		
		@Override
		public void execute(Board board) {
			boolean countdownReset = false;
			if (victim != null) {
				victimField.removePiece(true);
				board.removePiece(victim);
				
				if (victim instanceof RookPiece) {
					((RookPiece) victim).kill();
				}
				countdownReset = true;
			}
			updateBoard(board, countdownReset);
			
			boolean c = pawn.getColor();
			board.removePiece(pawn);

			startField.removePiece(false);			
			super.startField.forceRemove();
			if (queen == null) {
				this.setQueenReally(new QueenPiece(board, field, c, false));
			} else {
				queen.createSymbol();
				board.addPiece(queen);
				field.setPiece(queen);
			}
			fenBoard = FenParser.getBoard(board);

		}

		@Override
		public void undo(Board board) {
			startField.restorePiece(pawn);
			piece = pawn;
			board.addPiece(pawn); // new
			board.removePiece(queen);
			if (victim != null) {
				victimField.restorePiece(victim);
				board.addPiece(victim);
				
				if (victim instanceof RookPiece) {
					((RookPiece) victim).revive();
				}
			} else {
				field.removePiece(false);
				field.forceRemove();
			}
					
			resetBoard(board);
		}
		

		
		
		@Override
		public void executeSimulation(Board board, List<Piece> otherPieces, List<Piece> myPieces, Move thisMove) {
			super.executeSimulation(board, otherPieces);
			Piece piece = thisMove.getPiece();
			myPieces.remove(piece);
			piece = new QueenPiece(board, thisMove.getField(), piece.getColor(), true);
			queen = piece;
			myPieces.add(piece);
		}
				
		@Override
		public void undoSimulation(Board board, List<Piece> otherPieces, List<Piece> myPieces, Move thisMove) {
			super.undoSimulation(board, otherPieces);
			myPieces.remove(queen);
			myPieces.add(thisMove.getPiece());
		}

}
