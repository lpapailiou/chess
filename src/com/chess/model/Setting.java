package com.chess.model;

import java.io.File;
import java.util.List;
import com.chess.root.FenParser;
import com.chess.root.PgnParser;
import javafx.scene.layout.GridPane;

public class Setting {

	private GridPane boardGrid;
	private boolean whiteUp;
	private Mode mode;
	private PieceValues pieceValue;
	private Difficulty difficulty;
	private boolean touched = true;
	private boolean enpassantEnabled = true;
	private boolean timeout = true;
	private String[][] fenBoard;
	private boolean blackPlays;
	private String castlingOptions;
	private String passingPiece;
	private int countdown;
	private Double moveCounter = 1.0;
	private String[] pgnMeta;
	private List<String> pgnMoves;
	private boolean hasPgn;
	private boolean hasFen;

	public Setting(boolean whiteUp, Mode mode, PieceValues pieceValue, Difficulty difficulty) {
		this.whiteUp = whiteUp;
		this.mode = mode;
		this.pieceValue = pieceValue;
		this.difficulty = difficulty;
	}
	
	// ---------------------------------- PGN HANDLING ----------------------------------
	
	public boolean hasPgn() {
		return hasPgn;
	}
	
	public void addPgn(File file) {
		String s = PgnParser.getFileContent(file);
		addPgn(s);
	}
	
	public void addPgn(String s) {
		hasPgn = true;
		setPgnMeta(s);
		setPgnMoves();
	}

	private void setPgnMeta(String s) {
		if (s != null) {
			pgnMeta = s.split("]");
		}
	}
	
	public String[] getPgnMeta() {
		return pgnMeta;
	}
	
	private void setPgnMoves() {
		String pgnMoveData = null;
		if (pgnMeta != null) {
			pgnMoveData = pgnMeta[pgnMeta.length-1];
		}
		if (pgnMoveData != null) {
			pgnMoves = PgnParser.parseMoves(pgnMoveData);
		}
	}
	
	public String getDifficultyName() {
		return difficulty.get();
	}
	
	public String getWhite() {
		String player = PgnParser.getWhite(this);
		if (player == null) {	
			if (mode == Mode.AI_ONLY || (mode == Mode.MANUAL_VS_AI && !whiteUp)) {
				player = "Thought, Deep";
			} else {
				player = "Doe, Jane";
			}
		}
		return player; 
	}
	
	public String getBlack() {
		String player = PgnParser.getBlack(this);
		if (player == null) {	
			if (mode == Mode.AI_ONLY || (mode == Mode.MANUAL_VS_AI && whiteUp)) {
				player = "Blue, Deep";
			} else {
				player = "Doe, John";
			}
		}
		return player; 
	}

	public List<String> getPgnMoves() {
		return pgnMoves;
	}
	
	// ---------------------------------- FEN HANDLING ----------------------------------
	
	public String[][] getFenBoard() {
		return fenBoard;
	}
	
	public void setFenBoard(String[][] f) {
		if (f != null) {
			hasFen = true;
			fenBoard = f;
		}
	}
	
	public boolean hasFen() {
		return hasFen;
	}
	
	public void setCompleteFen(String[] completeFen) {
		for (int i = 0; i < completeFen.length; i++) {
			switch(i) {
				case 1:
					blackPlays = completeFen[i].contentEquals("b");
					break;
				case 2:
					castlingOptions = completeFen[i];
					break;
				case 3:
					passingPiece = completeFen[i]; 
					break;
				case 4:
					countdown = FenParser.parseInteger(completeFen[i]);
					break;
				case 5:
					moveCounter = FenParser.parseMoveCounter(completeFen[i]); 
					break;
				default:
					break;
			}
		}
	}
	
	public boolean getFenPlayer() {
		return blackPlays;
	}
	public String getFenCastlingOptions() {
		return castlingOptions;
	}
	public String getFenPassingPiece() {
		return passingPiece;
	}
	public int getFenCountdown() {
		int c = countdown;
		c *= 2;
		if (blackPlays) {
			c++;
		}
		return c;
	}
	public Double getFenMoveCounter() {
		if (moveCounter != null) {
			return moveCounter;
		}
		return 1.0;
	}
	
	public String getFenMoveCounterString() {
		if (moveCounter != null) {
			return moveCounter.toString();
		}
		return "1.0";
	}
	
	// ---------------------------------- GENERIC SETTERS AND GETTERS ----------------------------------
	
	public void setGrid(GridPane boardGrid) {
		this.boardGrid = boardGrid;
	}

	public GridPane getGrid() {
		return boardGrid;
	}

	public void setColor(boolean whiteUp) {
		this.whiteUp = whiteUp;
	}

	public boolean getColor() {
		return whiteUp;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public Mode getMode() {
		return mode;
	}

	public void setPieceValue(PieceValues pieceValue) {
		this.pieceValue = pieceValue;
	}

	public PieceValues getPieceValue() {
		return pieceValue;
	}
	
	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	public Difficulty getDifficulty() {
		return this.difficulty;
	}
	
	public boolean getPassing() {
		return enpassantEnabled;
	}
	
	public void setPassing(boolean passing) {
		enpassantEnabled = passing;
	}
	
	public boolean getTouched() {
		return touched;
	}
	
	public void setTouched(boolean touched) {
		this.touched = touched;
	}
	
	public boolean getTimeout() {
		return timeout;
	}
	
	public void setTimeout(boolean timeout) {
		this.timeout = timeout;
	}
}
