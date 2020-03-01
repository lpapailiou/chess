package com.chess.application;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import com.chess.model.Difficulty;
import com.chess.model.PieceValues;
import com.chess.model.Mode;
import com.chess.model.Setting;
import com.chess.root.FenParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SettingsController extends SceneController implements Initializable {
	
	// ---------------------------------- MENU ----------------------------------
	
	// exitItem		in SceneController
	
	// manualItem	in SceneController
	
	// rulesItem	in ScemeController
	
	// ---------------------------------- GUI ----------------------------------
	   	
	@FXML
	private BorderPane settingsPane;
	
	@FXML 
	private Label welcomeLabel;
	
	// colors
	@FXML 
	private Label colorLabel;
	
	@FXML 
	private RadioButton whiteChoice;
	
	@FXML 
	private RadioButton blackChoice;
	
	// rules
	@FXML 
	private Label ruleLabel;
	
	@FXML 
	private CheckBox touchedRuleChoice;
	
	@FXML 
	private CheckBox enPassantRuleChoice;
	
	@FXML 
	private CheckBox timeoutRuleChoice;
	
	// mode	
	@FXML 
	private Label modeLabel;
	
	@FXML 
	private ComboBox<String> modeChoice;
	
	// difficulty
	@FXML 
	private Label difficultyLabel;
	
	@FXML
	private Slider difficultyChoice;
	
	// load / start game
	@FXML 
	private Button loadButton;
	
	@FXML 
	private Label loadLabel;
	
	@FXML 
	private Button startButton;
	
	// ---------------------------------- INITIALIZATION ----------------------------------

	@Override
	public void initialize(URL location, ResourceBundle resources) {	
		super.initialize(location, resources);
		super.settings = new Setting(true, Mode.MANUAL_VS_AI, PieceValues.SUPERSUPERHARD, Difficulty.SUPERSUPERHARD);
		selectWhite();
		populateComboBoxe();
		populateSlider();
		prepareCSS();
	}
	
	// ---------------------------------- COLOR CHOICE HANDLING ----------------------------------

	@FXML
	private void handleWhiteRadio() {
		if (whiteChoice.isSelected()) {
			blackChoice.setSelected(false);
			whiteChoice.setSelected(true);
			super.settings.setColor(true);
		} else {
			blackChoice.setSelected(true);
			whiteChoice.setSelected(false);
			super.settings.setColor(false);
		}
	}

	@FXML
	private void handleBlackRadio() {
		if (blackChoice.isSelected()) {
			blackChoice.setSelected(true);
			whiteChoice.setSelected(false);
			super.settings.setColor(false);
		} else {
			blackChoice.setSelected(false);
			whiteChoice.setSelected(true);
			super.settings.setColor(true);
		}
	}
	
	// ---------------------------------- RULE CHOICE HANDLING ----------------------------------
	
	@FXML
	private void handleTouchedRuleChoice() {
		super.settings.setTouched(touchedRuleChoice.isSelected());
	}
	
	@FXML
	private void handleEnPassantRuleChoice() {
			super.settings.setPassing(enPassantRuleChoice.isSelected());
	}
	
	@FXML
	private void handleTimeoutRuleChoice() {
		super.settings.setTimeout(timeoutRuleChoice.isSelected());
	}

	// ---------------------------------- MODE CHOICE HANDLING ----------------------------------

	@FXML
	private void handleModeChoice() {
		String inputMode = modeChoice.getSelectionModel().getSelectedItem();
		if (inputMode.contentEquals(Mode.MANUAL_ONLY.get())) {
			difficultyLabel.setVisible(false);
			difficultyChoice.setVisible(false);
			super.settings.setMode(Mode.MANUAL_ONLY);
		} else {
			difficultyLabel.setVisible(true);
			difficultyChoice.setVisible(true);
			if (inputMode.contentEquals(Mode.MANUAL_VS_AI.get())) {
				super.settings.setMode(Mode.MANUAL_VS_AI);
			} else {
				super.settings.setMode(Mode.AI_ONLY);
			}
		} 
	}

	// ---------------------------------- DIFFICULTY CHOICE HANDLING ----------------------------------

	public void getSliderValue(Number newValue) {
		int value = newValue.intValue();
		PieceValues pieceVal = PieceValues.SUPERSUPERHARD;
		Difficulty difficultyVal = Difficulty.SUPERSUPERHARD;
		switch (value) {
		case 0:
			pieceVal = PieceValues.SUICIDE;
			difficultyVal = Difficulty.SUICIDE;
			break;
		case 10:
			pieceVal = PieceValues.RANDOM;
			difficultyVal = Difficulty.RANDOM;
			break;
		case 20:
			pieceVal = PieceValues.SUPEREASY;
			difficultyVal = Difficulty.SUPEREASY;
			break;
		case 30:
			pieceVal = PieceValues.VERYEASY;
			difficultyVal = Difficulty.VERYEASY;
			break;
		case 40:
			pieceVal = PieceValues.EASY;
			difficultyVal = Difficulty.EASY;
			break;
		case 50:
			pieceVal = PieceValues.MEDIUM;
			difficultyVal = Difficulty.MEDIUM;
			break;
		case 60:
			pieceVal = PieceValues.MEDIUMER;
			difficultyVal = Difficulty.MEDIUMER;
			break;
		case 70:
			pieceVal = PieceValues.HARD;
			difficultyVal = Difficulty.HARD;
			break;
		case 80:
			pieceVal = PieceValues.HARDER;
			difficultyVal = Difficulty.HARDER;
			break;
		case 90:
			pieceVal = PieceValues.SUPERHARD;
			difficultyVal = Difficulty.SUPERHARD;
			break;
		case 100:
			pieceVal = PieceValues.SUPERSUPERHARD;
			difficultyVal = Difficulty.SUPERSUPERHARD;
			break;
		default:
			break;
		}
		settings.setPieceValue(pieceVal);
		settings.setDifficulty(difficultyVal);
	}
	
	// ---------------------------------- LOAD DIALOG HANDLING ----------------------------------

	@FXML
	private void handleLoadButton(ActionEvent event) {
		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("Load game");
		dialog.setHeaderText("Load a game from FEN code or PGN code / file.");
		dialog.setResizable(true);
		dialog.getDialogPane().setMinWidth(500);
		dialog.getDialogPane().getStylesheets().add(SettingsController.class.getClassLoader().getResource("com/chess/resources/application.css").toExternalForm());

		GaussianBlur blurEffect = new GaussianBlur(2);
		settingsPane.setEffect(blurEffect);
		
		Label fenLabel = new Label("FEN: ");
		Label pgnLabel = new Label("PGN: ");
		TextField fenCode = new TextField();
		TextArea pgnCode = new TextArea();
		Label pgnFileName = new Label("no file selected");
		 
		GridPane grid = new GridPane();
		grid.add(fenLabel, 1, 1);
		grid.add(fenCode, 2, 1);
		grid.add(pgnLabel, 1, 2);
		grid.add(pgnCode, 2, 2);
		grid.setVgap(10);
		dialog.getDialogPane().setContent(grid);
		
		Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(new Image("com/chess/resources/img/go.png"));
		
		fenLabel.setMinWidth(70);
		pgnLabel.setMinWidth(70);
		fenCode.setPrefWidth(410);
		pgnCode.setPrefWidth(410);
		pgnCode.setPrefHeight(200);
		
		fenCode.setPromptText("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		pgnCode.setPromptText("[Event \"Casual waste of time\"]\r[Site \"Your cave, SWITZERLAND\"]\r[Date \"2019.12.08\"]\r[Round \"2\"]\r[White \"Thought, Deep\"]\r[Black \"Blue, Deep\"]\r[Result \"0:1\"]\r\r1. f4 d5 2. Nc3 d4 3. Nb5 a6 4. Na3 Bg4 5. h3 Bh5 6. d3 Nd7 \r7. Bd2 e6 8. Nc4 Qh4+ 9. g3 Qxg3# 0:1");
		dialog.getDialogPane().requestFocus();
		
		ButtonType buttonUpload = new ButtonType("Upload PGN file", ButtonData.OTHER);
		dialog.getDialogPane().getButtonTypes().add(buttonUpload);
		ButtonType buttonOk = new ButtonType("OK", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(buttonOk);
		ButtonType buttonCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().add(buttonCancel);
		
		ButtonBar buttonBar = (ButtonBar)dialog.getDialogPane().lookup(".button-bar");
		buttonBar.getButtons().forEach(b -> b.getStyleClass().add("gui-control-default"));

		dialog.setResultConverter(new Callback<ButtonType, String>() {
			@Override
			public String call(ButtonType b) {
				return convertResult(b, buttonOk, buttonUpload, pgnFileName, pgnCode, fenCode);
			}
		});
		  		
		Optional<String> result = dialog.showAndWait();
		 
		result.ifPresent(it -> {
			settingsPane.setEffect(null);
			if (it == null || it.contentEquals("")) {
				loadLabel.setText("NOPE! try again.");
				return;
			}

			if (it.contentEquals(pgnFileName.getText())) {
				String fileName = it;
				final File uploadFile = pngUploadFile;
				super.settings.addPgn(uploadFile);
				loadLabel.setText(fileName);
			} else if (pgnCode.getText() != null && it.startsWith("[")) {
				super.settings.addPgn(it);
				loadLabel.setText("LOAD !!!1!!111!!");
			} else {
				String[] fenArray = it.split(" ");
				String boardFen = fenArray[0];
				String[][] fen = FenParser.parseBoard(fenArray[0]);
				if (boardFen.contains("K") && boardFen.contains("k") && fen != null) {
					settings.setFenBoard(fen);
					settings.setCompleteFen(fenArray);
					loadLabel.setText("LOAD !!!1!!111!!");
				} else {
					loadLabel.setText("NOPE! try again.");
				}
			}
		});
			
		if (!result.isPresent()) {
			settingsPane.setEffect(null);
		}		
	}
	
	private String convertResult(ButtonType b, ButtonType buttonOk, ButtonType buttonUpload, Label pgnFileName, TextArea pgnCode, TextField fenCode) {
		if (b == buttonOk) {
			if (!pgnFileName.getText().contentEquals("no file selected")) {
				return pgnFileName.getText();
			} else if (pgnCode.getText() != null && !pgnCode.getText().contentEquals("")) {
				return pgnCode.getText();
			}
			return fenCode.getText();
		} else if (b == buttonUpload) {
			FileChooser fileChooser = new FileChooser();
			pngUploadFile = fileChooser.showOpenDialog(stage);
			if (pngUploadFile != null) {
				pgnFileName.setText(pngUploadFile.getName());
				return pgnFileName.getText();
			}
			
		}
		return null;
	}
	
	// ---------------------------------- START GAME HANDLING ----------------------------------

	@FXML 
	private void handleStartButton() {
		super.startNewGame(this, stage, settings);
	}

	// ---------------------------------- INITIALIZATION HELPERS ----------------------------------

 	private void selectWhite() {
 		whiteChoice.setSelected(true);
 		whiteChoice.requestFocus();
 	}

 	private void populateComboBoxe() {
 		modeChoice.getItems().addAll(Mode.MANUAL_VS_AI.get(), Mode.MANUAL_ONLY.get(), Mode.AI_ONLY.get());
 		modeChoice.getSelectionModel().select(Mode.MANUAL_VS_AI.get());
 	}

 	private void populateSlider() {
 		difficultyChoice.setMin(0);
 		difficultyChoice.setMax(100);
 		difficultyChoice.setValue(100);
 		difficultyChoice.setShowTickMarks(true);
 		difficultyChoice.setMinorTickCount(4);
 		difficultyChoice.setMajorTickUnit(50);
 		difficultyChoice.setBlockIncrement(10);
 		difficultyChoice.valueProperty().addListener((observable, oldValue, newValue) -> 
 			getSliderValue(newValue)
 		);
 	}

 	private void prepareCSS() {
 		String settingsLabel = "settings-label";
 		String playerRadios = "player-radios";
 		String cursorHand = "cursor-hand";
 		String guiControl = "gui-control-default";
 		welcomeLabel.getStyleClass().add("welcome-label");
 		colorLabel.getStyleClass().add(settingsLabel);
 		blackChoice.getStyleClass().add(playerRadios);
 		whiteChoice.getStyleClass().add(playerRadios);
 		ruleLabel.getStyleClass().add(settingsLabel);
 		touchedRuleChoice.getStyleClass().add(cursorHand);
 		enPassantRuleChoice.getStyleClass().add(cursorHand);
 		timeoutRuleChoice.getStyleClass().add(cursorHand);
 		modeLabel.getStyleClass().add(settingsLabel);
 		difficultyLabel.getStyleClass().add(settingsLabel);
 		startButton.getStyleClass().add("button-start");
 		modeChoice.getStyleClass().add(guiControl);
 		loadButton.getStyleClass().add(guiControl);
 		startButton.getStyleClass().add(guiControl);
 	}
 	
}
