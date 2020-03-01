package com.chess.application;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.logging.Level;
import com.chess.model.Mode;
import com.chess.model.Setting;
import com.chess.root.Game;
import com.chess.root.PgnParser;
import com.chess.root.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public final class GameController extends SceneController implements Initializable {
	
	private SettingsController settingsController;
	private boolean botActive = false;
	private long startTime;
	private String gameStatusText;
	private String[] alpha = {"a", "b", "c", "d", "e", "f", "g", "h"};
	
	// ---------------------------------- MENU ----------------------------------
	
	// game
	@FXML
	private MenuItem newGameItem;
	
	@FXML
	private MenuItem restartItem;
	
	@FXML
	private MenuItem fenCopyItem;
	
	@FXML
	private MenuItem pgnCopyItem;
	
	@FXML
	private MenuItem pgnExportItem;
	
	@FXML
	private MenuItem boarCopyItem;
		
	// exitItem		in SceneController
	
	// edit
	@FXML
	private MenuItem rotateBoardItem;
	
	@FXML
	private MenuItem editModeStartItem;
	
	@FXML
	private MenuItem editModeStopItem;
	
	// help
	@FXML
	private MenuItem dummyModeOnItem;
	
	@FXML
	private MenuItem dummyModeOffItem;
	
	@FXML
	private MenuItem movesItem;
		
	// manualItem	in SceneController
	
	// rulesItem	in ScemeController
		
	// ---------------------------------- GUI ----------------------------------
		
	// board
	@FXML
	private GridPane boardGrid;
	
	@FXML
	private VBox leftLabels;	// 1-8
	
	@FXML
	private VBox rightLabels;	// 1-8
	
	@FXML
	private HBox topLabels;		// a-h
	
	@FXML
	private HBox bottomLabels;	// a-h
	
	// edit mode bar
	@FXML
	private CheckBox manageEditHandler = new CheckBox();
	
	@FXML
	private HBox editBar;
	
	@FXML
	private Button goButton;
	
	@FXML
	private Button stopButton;
	
	@FXML
	private Button stepBackButton;
	
	@FXML
	private Button stepForwardButton;
	
	@FXML
	private Label speedLabel;
	
	@FXML
	private Slider speedSlider;
	
	// status bar
	@FXML
	private Label statusLabel;
	
	@FXML
	private Label statusTextLabel;
	
	@FXML
	private Label moveCounter;
	
	// ---------------------------------- INITIALIZATION ----------------------------------
	
	public void initializeProxy(SettingsController init, Setting settings) {
		this.settings = settings;
		if (settings.getMode() != Mode.MANUAL_ONLY) {
			botActive = true;
		}
		
		settings.setGrid(boardGrid);
		this.stage = init.getStage();
		this.chess = init.getMainAccess();
		this.settingsController = init;
		moveCounter.setText(settings.getFenMoveCounterString());
		displayPlayer(settings.getFenPlayer(), moveCounter.getText());
		populateSlider();
		editModeStopItem.setDisable(true);
		dummyModeOffItem.setDisable(true);
		setUpEditMode();
	
		setTooltips();
		this.game = new Game(this, settings);
		
		statusLabel.setOnMouseClicked((MouseEvent event) -> {
			game.getBoard().showLastMove();
			event.consume();
		});
		
		statusTextLabel.setOnMouseClicked((MouseEvent event) -> {
			if (game != null && game.getBoard() != null) {
				game.getBoard().showPossibleMoves();
			}
			event.consume();
		});
	
	}
	
	// ---------------------------------- INITIALIZATION HELPERS ----------------------------------
	
	private void setTooltip(Label element, String text) {
		Tooltip tip = new Tooltip();
		tip.setText(text);
		tip.setStyle("-fx-font-size: 12pt;");
		element.setTooltip(tip);
	}
	
	private void setTooltips() {
		setTooltip(statusLabel, "click here to see last move");
		setTooltip(statusTextLabel, "click here to see which pieces are able to move");
	}
	
	private ImageView createSymbol(String suffix) {
		String path = "com/chess/resources/img/";
		path += suffix;
		final String filePath = path;
		ImageView img = new ImageView(filePath);
		img.setFitWidth(16);
		img.setFitHeight(16);
		return img;
	}
	
	public void populateSlider() {
		speedSlider.setMin(0);
		speedSlider.setMax(300);
		speedSlider.setCursor(Cursor.HAND);
		speedSlider.setValue(0);
 		speedSlider.setShowTickMarks(false);
 		speedSlider.setMinorTickCount(100);
 		speedSlider.setMajorTickUnit(100);
 		speedSlider.setBlockIncrement(100);
 		speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> 
 			getSliderValue(newValue)
	 	);
 	}
	
	private void setUpEditMode() {
		stopButton.setGraphic(createSymbol("stop.png"));
		goButton.setGraphic(createSymbol("go.png"));
		stepBackButton.setGraphic(createSymbol("backward.png"));
		stepForwardButton.setGraphic(createSymbol("forward.png"));
		setGoBut(false);
		setBackBut(false);
		setForwardBut(false);
		manageEditHandler.setSelected(false);
		editBar.managedProperty().bind(manageEditHandler.selectedProperty());
	
		goButton.setVisible(false);
		stopButton.setVisible(false);
		stepBackButton.setVisible(false);
		stepForwardButton.setVisible(false);
		speedLabel.setVisible(false);
		speedSlider.setVisible(false);
		
		String guiClass = "gui-control-default";
		goButton.getStyleClass().add(guiClass);
		stopButton.getStyleClass().add(guiClass);
		stepBackButton.getStyleClass().add(guiClass);
		stepForwardButton.getStyleClass().add(guiClass);
		
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), (ActionEvent event) -> {
			if (stepBackButton.isPressed()) {
				if (System.currentTimeMillis() - startTime > 500) {
					game.stepBack();
					setForwardBut(true);
				}
			} else if (stepForwardButton.isPressed()) {
				if (System.currentTimeMillis() - startTime > 500) {
					game.stepForward();
				}
			} else {
				startTime = System.currentTimeMillis();
			}
		 }));
		 timeline.setCycleCount(Timeline.INDEFINITE);
		 timeline.play();	
	}
	
	// ---------------------------------- GAME HANDLING ----------------------------------
	
	@FXML 
	private void handleSettings(ActionEvent event) {
		resetStage();
		cleanup();
		chess.getSettingsScreen(stage);
	}
	
	@FXML 
	private void handleRestart(ActionEvent event) {
		resetStage();
		cleanup();
		super.startNewGame(settingsController, stage, settings);
	}
	
	@FXML
	@Override
	public void handleExit(ActionEvent event) {
		cleanup();
		super.handleExit(event);
	}
	
	private void cleanup() {
		List<Player> players = game.getAIPlayers();
		for (Player player : players) {
			player.getThread().requestStop();
		}
	}
	
	// ---------------------------------- EXPORT HANDLING ----------------------------------
	
	@FXML
	private void handleFenCopy() {
		String fen = game.getBoard().getFen();
		ClipboardContent cc = new ClipboardContent();
	cc.putString(fen);
	Clipboard.getSystemClipboard().setContent(cc);
	}
	
	@FXML
	private void handlePgnCopy() {
		String pgn = PgnParser.getFullPgn(game);
		ClipboardContent cc = new ClipboardContent();
	cc.putString(pgn);
	Clipboard.getSystemClipboard().setContent(cc);
	}
	
	@FXML
	private void handlePgnExport() {
		try {  
			pgnString = PgnParser.getFullPgn(game);
			InputStream htmlFile = new ByteArrayInputStream(pgnString.getBytes());
			if (pgnFile == null || pgnPath == null) { 
				 pgnFile = File.createTempFile("chess_game", ".pgn");
				 pgnPath = pgnFile.toPath(); 
			 }
			Files.copy(htmlFile, pgnPath, StandardCopyOption.REPLACE_EXISTING);
			pgnFile.deleteOnExit();
			URI url = pgnFile.toURI();
			Desktop.getDesktop().browse(url);  
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}
	}
	
	@FXML
	private void handleBoardCopy() {
		WritableImage image = boardGrid.snapshot(new SnapshotParameters(), null);
		ClipboardContent cc = new ClipboardContent();
		cc.putImage(image);
		Clipboard.getSystemClipboard().setContent(cc);
	}
	
	// ---------------------------------- EDIT MODE HANDLING ----------------------------------
	
	@FXML
	protected void handleRotate() {
		if (boardGrid.getRotate() == 180.0) {
			boardGrid.setRotate(0.0);
			for (Node node : boardGrid.getChildren()) {
			((FieldButton)node).setRotate(0.0);
		}
			unswitchBoard();
		} else {
			boardGrid.setRotate(180.0);
			for (Node node : boardGrid.getChildren()) {
			((FieldButton)node).setRotate(180.0);
		}
			switchBoard();
		}
	}
	
	private void switchBoard() {
		for (int i = 0; i < 8; i++) {
			((Labeled) leftLabels.getChildren().get(i)).setText(Integer.toString(i+1));
			((Labeled) rightLabels.getChildren().get(i)).setText(Integer.toString(i+1));
			((Labeled) topLabels.getChildren().get(i)).setText(alpha[7-i]);
			((Labeled) bottomLabels.getChildren().get(i)).setText(alpha[7-i]);
		}
	}
	
	private void unswitchBoard() {
		for (int i = 0; i < 8; i++) {
			((Labeled) leftLabels.getChildren().get(i)).setText(Integer.toString(8-i));
			((Labeled) rightLabels.getChildren().get(i)).setText(Integer.toString(8-i));
			((Labeled) topLabels.getChildren().get(i)).setText(alpha[i]);
			((Labeled) bottomLabels.getChildren().get(i)).setText(alpha[i]);
		}
	}
	
	@FXML 
	private void handleStartEditMode(ActionEvent event) {
		pauseGame();
		setEditMode(true);
		getStage().setMinHeight(692);
		editModeStartItem.setDisable(true);
		editModeStopItem.setDisable(false);
		setEditableGui(false);
	}
	
	@FXML 
	private void handleLeaveEditMode(ActionEvent event) {
		setStopBut(true);
		setGoBut(false);
		setBackBut(false);
		setForwardBut(false);
		setEditMode(false);
		game.resumeGame();
		resetStage();
		editModeStartItem.setDisable(false);
		editModeStopItem.setDisable(true);
		setEditableGui(true);
	}
	
	private void setEditMode(boolean on) {
		manageEditHandler.setSelected(on);
		if (botActive) {
			speedLabel.setVisible(on);
			speedSlider.setVisible(on);
		}
		stopButton.setVisible(on);
		goButton.setVisible(on);
		stepBackButton.setVisible(on);
		stepForwardButton.setVisible(on);
	}
	
	public void getSliderValue(Number newValue) {
		int value = newValue.intValue();
		game.setSpeed(300 - value);
	}
	
	private void resetStage() {
		getStage().setMinHeight(669);
		getStage().setHeight(669);
	}
	
	// ---------------------------------- HELP HANDLING ----------------------------------
	
	@FXML 
	private void handleDummyOn(ActionEvent event) {
		dummyModeOnItem.setDisable(true);
		dummyModeOffItem.setDisable(false);
		game.setDummyMode(true);
	}
	
	@FXML 
	private void handleDummyOff(ActionEvent event) {
		dummyModeOnItem.setDisable(false);
		dummyModeOffItem.setDisable(true);
		game.setDummyMode(false);
	}
	
	@FXML
	private void showMoves() {
		try {  
			 if (movesFile == null || movesPath == null) {
				 InputStream htmlFile = new ByteArrayInputStream(out.getBytes());
				 movesFile = new File(System.getProperty("java.io.tmpdir"), "chess_moves.html");
				 movesPath = movesFile.toPath();
				 Files.copy(htmlFile, movesFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			 }
			 movesFile.deleteOnExit();
			 URI url = movesFile.toURI();
			 Desktop.getDesktop().browse(url); 
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}
	}

	// ---------------------------------- EDIT BAR HANDLING ----------------------------------
	
	@FXML 
	private void handleGo(ActionEvent event) {
		setStopBut(true);
		setGoBut(false);
		setBackBut(false);
		setForwardBut(false);
		game.resumeGame();
		setEditableGui(true);
	}
	
	@FXML 
	private void handleStop(ActionEvent event) {
		pauseGame();
		setEditableGui(false);
	}
	
	private void pauseGame() {
		setStopBut(false);
		setGoBut(true);
		setForwardBut(false);
		game.pauseGame();
		setEditableGui(false);
	}
	
	@FXML 
	private void handleStepBack(ActionEvent event) {
		game.stepBack();
	}
	
	@FXML 
	private void handleStepForward(ActionEvent event) {
		game.stepForward();
	}
	
	private void setEditableGui(boolean yes) {
		Cursor c;
		if (yes) {
			c = Cursor.HAND;
		} else {
			c = Cursor.DISAPPEAR;
		}
		for (Node node : boardGrid.getChildren()) {
			((FieldButton)node).setCursor(c);
		}
	}
	
	// ---------------------------------- EDIT PUBLIC CONTROLS ----------------------------------
	
	public void setGoBut(boolean activate) {
		goButton.setDisable(!activate);
		if (activate) {
			goButton.setCursor(Cursor.HAND);
		} else {
			goButton.setCursor(Cursor.DISAPPEAR);
		}
	}
	
	public void setStopBut(boolean activate) {
		stopButton.setDisable(!activate);
		if (activate) {
			stopButton.setCursor(Cursor.HAND);
		} else {
			stopButton.setCursor(Cursor.DISAPPEAR);
		}
	}
	
	public void setBackBut(boolean activate) {
		stepBackButton.setDisable(!activate);
		if (activate) {
			stepBackButton.setCursor(Cursor.HAND);
		} else {
			stepBackButton.setCursor(Cursor.DISAPPEAR);
		}
	}
	
	public void setForwardBut(boolean activate) {
		stepForwardButton.setDisable(!activate);
		if (activate) {
			stepForwardButton.setCursor(Cursor.HAND);
		} else {
			stepForwardButton.setCursor(Cursor.DISAPPEAR);
		}
	}
	
	public void requestFocusGo() {
		goButton.requestFocus();
	}
	
	public void requestFocusStop() {
		stopButton.requestFocus();
	}
	
	public void requestFocusBack() {
		stepBackButton.requestFocus();
	}
	
	public void requestFocusForward() {
		stepForwardButton.requestFocus();
	}
	
	// ---------------------------------- STATUS BAR HANDLING ----------------------------------
	
	public void displayPlayer(boolean player, String c) {
		if (c.contentEquals("1.0")) {
			setDisplay((player ? "black" : "white") + " starts the game");
		} else {
			setDisplay((player ? "black" : "white") + "'s turn");
		}
	}
	
	public void displayPlayer(Game game) {
		if (game != null && game.getPlayer() != null) {
			setDisplay(game.getPlayer().toString() + "'s turn");
		} 
	}
	
	public void setDisplay(String display) {
		gameStatusText = display;
		renderDisplay();		
	}
	
	public String getDisplay() {
		return gameStatusText;
	}
	
	public void renderDisplay() {
		if (Platform.isFxApplicationThread()) {
			statusTextLabel.setText(gameStatusText);
		} else {
			Platform.runLater(() -> 
				statusTextLabel.setText(gameStatusText)
			);
		}
	}
	
	public void updateMoveCounter(String counter) {
		Platform.runLater(() -> 
			moveCounter.setText(counter)
		);
	}

}
