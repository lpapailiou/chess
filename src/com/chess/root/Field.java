package com.chess.root;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.chess.application.FieldButton;
import com.chess.root.pieces.Piece;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class Field {
	private FieldButton button;
	private int column;
	private int row;
	private boolean isBlack = false;
	private Piece piece = null;
	private String notation = "";
	private static final Logger LOG = Logger.getLogger(String.class.getName());

	public Field(int column, int row, boolean isBlack) {	
		this.column = column;
		this.row = row;
		this.isBlack = isBlack;
		this.notation = getColNotation() + getRowNotation();
	}

	// ---------------------------------- GAMEPLAY HANDLING ----------------------------------
	
	public void setPiece(Piece piece) {
		if (piece == null) {
			throw new NullPointerException("piece is null" + this.toString());
		}
		setPiece(piece, false);
	}
	
	public void setPiece(Piece piece, boolean init) {
		if (piece == null) {
			throw new NullPointerException("piece is null" + this.toString());
		}
		if (this.piece == null || this.piece.equals(piece)) {
			this.piece = piece;	
			if(button != null) {
				updateButton();
			}
			// set current field as property in the Piece class
			setField(init);
		}
	}
	
	public void setRookPiece(Piece piece) {
		this.piece = piece;
		if(button != null) {
			updateButton();
		}
		this.piece.setFieldSilently(this);
	}
	
	public FieldButton getButton() {
		return button;
	}
	
	private void updateButton() {
		if (Platform.isFxApplicationThread()) {
			setButtonGraphic();
			
			// seems to be necessary...
			Platform.runLater(() -> 
				button.setText("")
			);
		} else {
			
			try {
				updateUI();
				
			} catch (InterruptedException | ExecutionException e) {
				LOG.log(Level.SEVERE, e.getMessage());
				Thread.currentThread().interrupt();
			} 
		}
	}
	
	public void setOpacity(Double d) {
		button.setOpacity(d);
		Platform.runLater(() -> 
			button.setOpacity(d)
		);
	}
	
	private void setField(boolean init) {
		if (piece == null) {
			return;
		}
		if (init) {
			this.piece.setFieldSilently(this);
		} else {
			
			this.piece.setField(this);
		}
	}

	public void restorePiece(Piece piece) {
		if (piece == null) {
			throw new NullPointerException("no piece found");
		}

		this.piece = piece;
		final ImageView x = piece.getSymbol();
		Platform.runLater(() -> 
			button.setText("o")
		);

		if (button != null) {
			
			updateButton();
			Platform.runLater(() -> 
				button.setGraphic(x)
			);
			
			Platform.runLater(() -> 
				button.getGraphic().setOpacity(1.0)
			);
		}
		this.piece.setFieldSilently(this);
	}
	
	public void render() {
		if (piece == null) {
			Platform.runLater(() -> 
				button.setGraphic(null)
			);
		} else {

			final ImageView x = piece.getSymbol();
			Platform.runLater(() -> 
				button.setText("o")
			);
	
			updateButton();
			Platform.runLater(() -> 
				button.setGraphic(x)
			);
			Platform.runLater(() -> 
				button.getGraphic().setOpacity(1.0)
			);
		}
	}

	public void removePiece(boolean isVictim) {
		if (this.piece != null) {
			this.piece = null;
			
			if(!isVictim && button != null && button.getGraphic() != null) {
				button.getGraphic().setOpacity(0.3);
			}
		}
	}
	
	public void forceRemove() {
		if(button != null) {
			Platform.runLater(() -> 
				clearButton()
			);	
		}
	}
	
	public void flash() {
		button.flash();
	}
	
	public void waitMode(boolean wait) {
		button.setWaitCursor(wait);
	}

	private void clearButton() {
		button.setGraphic(null);
		button.setText("");
	}
	
	public void removePiece() {
		if (this.piece != null) {
			this.piece = null;
			if(button != null) {
				button.setText("");
				Platform.runLater(() -> 
					clearButton()
				);	
			}
		}
	}

	public void setPieceSilently(Piece piece) {
		this.piece = piece;
	}

	public void removePieceSilently() {
		piece = null;
	}
	
	// ---------------------------------- GENERIC SETTERS AND GETTERS ----------------------------------
	
	public Piece getPiece() {
		return piece;
	}

	public int getColumn() {
		return column;
	}

	public String getColNotation() {
		String s = "";
		switch (column) {
			case 0:
				s = "a";
				break;
			case 1:
				s= "b";
				break;
			case 2:
				s = "c";
				break;
			case 3:
				s= "d";
				break;
			case 4:
				s = "e";
				break;
			case 5:
				s= "f";
				break;
			case 6:
				s = "g";
				break;
			case 7:
				s= "h";
				break;
			default:
				break;
		}
		return s;
	}
	
	public int getRowNotation() {
		int s = 0;
		switch (row) {
			case 0:
				s = 8;
				break;
			case 1:
				s= 7;
				break;
			case 2:
				s = 6;
				break;
			case 3:
				s= 5;
				break;
			case 4:
				s = 4;
				break;
			case 5:
				s= 3;
				break;
			case 6:
				s = 2;
				break;
			case 7:
				s= 1;
				break;
			default:
				break;
		}
		return s;
	}
	
	public String getFen() {
		if (piece != null) {
			return piece.getFen();
		}
		return null;
	}
	
	public String getNotation() {
		return notation;
	}

	public int getRow() {
		return row;
	}

	public boolean isBlack() {
		return isBlack;
	}

	public void setButton(FieldButton button) {
		this.button = button;
	}
	
	// ---------------------------------- GENERIC HELPER METHODS ----------------------------------

	private void updateUI() throws InterruptedException, ExecutionException{
		FutureTask<Void> updateUITask = new FutureTask<>(() -> 
			setButtonGraphic()
		, null);
		Platform.runLater(updateUITask);
		updateUITask.get();
	}
	
	private void setButtonGraphic() {
		button.setGraphic(piece.getSymbol());
		button.getGraphic().setOpacity(1.0);
	}

	@Override
	public String toString() {
		return "field " + getColumn() + getRow();
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj && this instanceof Field && obj instanceof Field;
	}

	@Override
	public int hashCode() {
		int hash = this.isBlack() ? 3 : 4;
		hash += this.toString().hashCode();
		hash += 123;
		return hash;
	}

}
