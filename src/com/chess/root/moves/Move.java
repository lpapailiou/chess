package com.chess.root.moves;

import java.util.List;
import com.chess.root.Board;
import com.chess.root.FenParser;
import com.chess.root.Field;
import com.chess.root.pieces.KingPiece;
import com.chess.root.pieces.PawnPiece;
import com.chess.root.pieces.Piece;
import com.chess.root.pieces.RookPiece;

public class Move {

	protected Piece piece;
	protected Field startField;
	
	protected Field field;
	
	protected Piece victim;
	protected Field victimField;
	
	protected Piece enPassant;

	
	protected int rating;
	private int countdown;
	
	protected String notation;
	
	protected String hitOrMove = "-";
	protected String notationSuffix = "";
	protected String checkSuffix = "";
	
	protected String fenBoard = "";
	
	private String result = "*";
	
	private boolean ambigCol = false;
	private boolean ambigRow = false;

	public Move(Piece piece, Field field, Piece victim) {
		this.piece = piece;
		this.startField = piece.getField();
		this.field = field;
		this.victim = victim;
		if (victim != null) {
			victimField = victim.getField();
		}
		updateNotation();
	}
		
	// ---------------------------------- PRE-CALCULATE BASE RATING ----------------------------------
	
	public String getResult() {
		return result;
	}
	
	public void ambiguousCol() {
		ambigCol = true;
	}
	
	public void ambiguousRow() {
		ambigRow = true;
	}
	
	public boolean isAmbiguousC() {
		return ambigCol;
	}
	public boolean isAmbiguousR() {
		return ambigRow;
	}
	
	
	public void setResult(String s) {
		result = s;
	}
		
	public void setCheckSuffix(String s) {
		checkSuffix = s;
	}
	
	public Piece getPassing() {
		return enPassant;
	}
	
	public String getTargetNotation() {
		return piece.getNotation() + field.getNotation();
	}
	
	public String getFenBoard() {
		return fenBoard;
	}
	
	
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
		
		startField.removePiece(false);
		
		field.setPiece(piece);
		
		startField.forceRemove();
		fenBoard = FenParser.getBoard(board);
	}
	
	public void executeSimulation(Board board, List<Piece> otherPieces, List<Piece> myPieces, Move thisMove) {
		executeSimulation(board, otherPieces);
	}
	
	public void executeSimulation(Board board, List<Piece> otherPieces) {
		if (victim != null) {
			victimField.removePieceSilently();
			otherPieces.remove(victim);
		}
		savePassing(board.getEnPassantPiece());
		board.setEnPassantPiece(null);
		startField.removePieceSilently(); 
		piece.setFieldSilently(field);
		field.setPieceSilently(piece);
	}
	
	public void undoSimulation(Board board, List<Piece> otherPieces, List<Piece> myPieces, Move thisMove) {
		undoSimulation(board, otherPieces);
	}
	
	public void undoSimulation(Board board, List<Piece> otherPieces) {
		startField.setPieceSilently(piece);
		piece.setFieldSilently(startField);
		board.setEnPassantPiece(enPassant);			
		if (victim != null) {
			victimField.setPieceSilently(victim);
			victim.setFieldSilently(victimField);
			otherPieces.add(victim);
		} else {
			field.removePieceSilently();
		}
	}
		
	public void undo(Board board) {
		startField.restorePiece(piece);

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
	
	protected void resetBoard(Board board) {
		board.setCountdown(countdown);
		board.setEnPassantPiece(enPassant);
		
		if (piece instanceof KingPiece) {
			KingPiece p = (KingPiece) piece;
			p.unmove();
		}
		
		if (piece instanceof RookPiece) {
			RookPiece p = (RookPiece) piece;
			p.unmove();
		}
	}
	
	protected void updateBoard(Board board, boolean countdownReset) {
		enPassant = board.getEnPassantPiece();
		board.setEnPassantPiece(null);
		
		if (piece instanceof PawnPiece) {
			countdownReset = true;
		}
		
		if (piece instanceof KingPiece) {
			KingPiece p = (KingPiece) piece;
			p.moved();
		}
		
		if (piece instanceof RookPiece) {
			RookPiece p = (RookPiece) piece;
			p.moved();
		}
		
		if (countdownReset) {
			this.countdown = board.getCountdown();
			board.setCountdown(0);
		}
	}

	public Piece getPawn() {
		return null;
	}
	
	protected void updateNotation() {
		if (victim != null) {
			hitOrMove = "x";
		}
		if (piece instanceof PawnPiece) {
			PawnPiece pawn = (PawnPiece) piece;
			if ((pawn.movesUp() && field.getRow() == 0) || (!pawn.movesUp() && field.getRow() == 7)) {
				notationSuffix += "Q";
			}
		}
	}
	
	public String print() {
		return this.piece + " " + this.startField.getNotation() + " to " + this.field.getNotation() + ", killed: " + this.victim + " (" + this.rating + ")";
	}
	
	public String getNotation() {
		if (notation == null) {
			notation = piece.getNotation() + startField.getNotation() + hitOrMove + field.getNotation() + notationSuffix;
		}
		return notation + checkSuffix;		
	}
	
	public String getPgnNotation() {
		String pgnNotation;
		String separator = "";
		String piecen = piece.getNotation();
		if (this instanceof PromotionMove) {
			piecen = "";
		}
		String suffix = "";
		if (hitOrMove.contentEquals("x")) {
			separator = hitOrMove;
			if (piece instanceof PawnPiece) {
				piecen = startField.getColNotation();
			}
		} else if (this instanceof PromotionMove) {
			suffix = "=Q";
		}
		if (ambigCol) {
			separator = startField.getColNotation() + separator;
		} else if (ambigRow) {
			separator = startField.getRowNotation() + separator;
		}
		pgnNotation = piecen + separator + field.getNotation() + suffix + checkSuffix;
	
		return pgnNotation;
	}
	
	public void savePassing(Piece piece) {
		enPassant = piece;
	}
	
	// ---------------------------------- GENERIC SETTERS AND GETTERS ----------------------------------

	public Piece getPiece() {
		return piece;
	}

	public Piece getVictim() {
		return victim;
	}

	public Field getField() {
		return field;
	}
	
	public Field getStartField() {
		return startField;
	}
	
	public Field getVictimField() {
		return victimField;
	}

	public int getRating() {
		return rating;
	}

	public void incRating(int rating) {
		this.rating += rating;
	}
	
	public void flash() {
		startField.flash();
		field.flash();
	}

}
