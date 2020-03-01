package com.chess.root.pieces;

import java.util.List;

import com.chess.root.Board;
import com.chess.root.Field;
import com.chess.root.moves.Move;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Piece {
	protected Board board;
	protected Field field;
	private String descriptiveName; // internal use only
	private String notation;
	protected String fen;
	private ImageView symbol;
	private Image image;
	protected boolean color;
	protected int rating;
	protected int posValue;
	protected int defense;
	private static final int IMGSIZE = 60;
	protected int[][] table;
		
	public Piece(Board board, Field field, boolean color, String name, String notation, int rating, int[][] table, boolean simulation) {
		this.board = board;
		if (field == null) {
			throw new NullPointerException("no field set!");
		}
		this.field = field;
		this.descriptiveName = name;
		this.notation = notation;
		this.color = color;
		this.rating = rating;
		this.table = table;
		
		if (!simulation) {
			createSymbol();
			init();
		}
	}
	
	public String getFen() {
		return fen;
	}
	
	public String getCastlingFen() {
		return "";
	}
	
	public void init() {
		field.setPiece(this, true);
		board.addPiece(this);
	}
	
	// ---------------------------------- GAMEPLAY HADNLING ----------------------------------
	
	public void setField(Field field) {
		if (field == null) {
			throw new NullPointerException("null value was set" + this.toString());
		}
		this.field = field;
		updatePos();
		board.endMove();
	}
	
	public void setFieldSilently(Field field) {
		this.field = field;
		updatePos();
	}
	
	private void updatePos() {
		posValue = table[field.getRow()][field.getColumn()];
	}
	
	// ---------------------------------- GENERIC GETTERS AND SETTERS ----------------------------------
	
	public Field getField() {
		if (field == null) {
			throw new NullPointerException("Field not found! " + this.toString());
		}
		return field;
	}

	public int getColumn() { 
		if (field == null) {
			throw new NullPointerException("Field column not found - out of range!" + this.toString());
		}
		return field.getColumn();
	}

	public int getRow() {
		if (field == null) {
			throw new NullPointerException("Field row not found - out of range!");
		}
		return field.getRow();
	}
		
	public boolean isBlack() {
		return color;
	}

	public boolean getColor() {
		return color;
	}

	public ImageView getSymbol() {
		return symbol;
	}
	
	public String getNotation() {
		return notation;
	}
	
	public String getPgnNotation() {
		return getNotation();
	}
	
	public int getRating() {
		return rating;
	}
	
	public int getValue() {
		return rating + posValue;
	}
	
	public boolean wasMoved() {
		return true;
	}
	
	public void moved() {
		
	}
	
	public void unmove() {
		
	}
	
	public boolean isDead() {
		return false;
	}
	
	public void setEndTable(boolean end) {
		
	}

	// ---------------------------------- HELPER METHODS ----------------------------------
	
	public void createSymbol() {
		if (image == null || symbol == null) {
			String path = "com/chess/resources/img/";
			path += descriptiveName;
			path += this.isBlack() ? "_b.png" : "_w.png";
			final String filePath = path;
			ImageView img = new ImageView(filePath);
			img.setFitWidth(IMGSIZE);
			img.setFitHeight(IMGSIZE);
			symbol = img;
			
			Double imgSize = (double) IMGSIZE;
			image = new Image(filePath, imgSize, imgSize, false, false);
		}
	}
	
	public Image getImage() {
		return image;
	}

	@Override
	public String toString() {
		return descriptiveName + "" + field.getNotation();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			if ((this instanceof PawnPiece && obj instanceof PawnPiece) || (this instanceof RookPiece && obj instanceof RookPiece) || (this instanceof KnightPiece && obj instanceof KnightPiece)) {
				return true;
			}
			if ((this instanceof BishopPiece && obj instanceof BishopPiece) || (this instanceof QueenPiece && obj instanceof QueenPiece) || (this instanceof KingPiece && obj instanceof KingPiece)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = this.isBlack() ? 2 : 0;
		hash += this.getField().getColumn();
		hash += this.getField().getRow();
		hash += this.toString().hashCode();
		hash += 123;
		return hash;
	}
	
	// ---------------------------------- ABSTRACT METHODS ----------------------------------
	
	public abstract List<Move> getMoves();

	public void initializeFenCastling(String cas) {
	}

}
