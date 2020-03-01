package com.chess.application;

import com.chess.root.Board;
import com.chess.root.Field;
import javafx.animation.FadeTransition;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.effect.Lighting;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Duration;

public class FieldButton extends Button {
	
	private Board board;
	private Field field;
	private String baseCSS = "-fx-background-radius: 0; -fx-font-weight: bold; -fx-font-size: 20; -fx-effect: dropshadow( one-pass-box , black , 8 , 0.0 , 2 , 0 ); -fx-effect: innershadow( gaussian , rgba(0,0,0,0.7) , 20,0,0,0 ); -fx-padding: 0;";
	private String hoverb = "-fx-background-color: #b2b2a0;";
	private String hoverw = "-fx-background-color: #e6e6d5;";
	
	public FieldButton(Board board, Field field) {
		this.board = board;
		this.field = field;
		this.setMaxWidth(Double.MAX_VALUE);
		this.setMaxHeight(Double.MAX_VALUE);
		Lighting light = new Lighting();
		light.setSurfaceScale(0.8f);
		this.setEffect(light);
		this.getStyleClass().add("button-default");
		this.setCursor(Cursor.HAND);
		
		addEvents();
	}

	// ---------------------------------- GUI HANDLING ----------------------------------
	
	public void setWaitCursor(boolean wait) {
		if (wait) {
			this.setCursor(Cursor.WAIT);
		} else {
			this.setCursor(Cursor.HAND);
		}
	}
	
	private void reset() {
		this.setStyle(null);
		this.getStyleClass().clear();
		if (field.isBlack()) {	
			this.getStyleClass().add("button-black");
		} else {
			this.getStyleClass().add("button-white");
		}
		this.getStyleClass().add("button-default");

	}
		
	private void setHover() {
		if (field.isBlack()) {
			this.setStyle(baseCSS + hoverb);
		} else {
			this.setStyle(baseCSS + hoverw);
		}
	}
	
	public void flash() {
		FadeTransition flash = new FadeTransition(Duration.seconds(1.0), this);
		flash.setFromValue(1.0);
		flash.setToValue(0.5);
		flash.play();
		FadeTransition reverse = new FadeTransition(Duration.seconds(1.0), this);
		reverse.setFromValue(0.5);
		reverse.setToValue(1.0);
		reverse.play();
	}
	
	// ---------------------------------- GENERIC METHODS ----------------------------------
	
	public Field getField() {
		return field;
	}
	
	public void setField(Field field) {
		this.field = field;
	}
	
	// ---------------------------------- INITIALIZATION ----------------------------------
	
	private void addEvents() {
		// add event handler for manual gameplay (triggering move)					
		setOnMouseClicked((MouseEvent event) -> {
			if (event.getButton() == MouseButton.SECONDARY) {
				board.showHints(event);
			} else if (board.isEditable()  && event.getButton() == MouseButton.PRIMARY) {
				board.performManualMove(event);
			}
			event.consume();
		});
		
		// first event, picks up source - actual drag
		setOnDragDetected((MouseEvent event) -> {
			if (board.isEditable()  && event.getButton() == MouseButton.PRIMARY) {
				board.performManualDrag(event);
			}
			event.consume();
		});
		
		// fifth event, on mouse release - actual drop
		setOnDragDropped((DragEvent event) -> {
			if (board.isEditable()) {
				board.performManualDrop(event);
				reset();
			}
			event.consume();
		});

		// second event, points out who will be the target
		setOnDragOver((DragEvent event) -> {
			if (board.isEditable()) {
				event.acceptTransferModes(TransferMode.MOVE);
			}
			event.consume();
		});
		
		// third event, for visual feedback entering a field
		setOnDragEntered((DragEvent event) -> {
			if (board.isEditable()) {
				setHover();
			}
			event.consume();
		});
		
		// fourth event, for visual feedback leaving a field
		setOnDragExited((DragEvent event) -> {
			if (board.isEditable()) {
				reset();
				this.setPressed(false);
			}
			event.consume();
		});
						
		// sixth event, when the drop is done
		setOnDragDone((DragEvent event) -> {
			if (board.isEditable()) {
				reset();
				this.setFocused(false);
				this.setPressed(false);
			}
			event.consume();
		});
	}

}
