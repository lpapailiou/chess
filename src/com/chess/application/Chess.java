package com.chess.application;
	
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Chess extends Application {
	
	private SettingsController controller;
	private static final Logger LOG = Logger.getLogger(String.class.getName());

	public void getSettingsScreen(Stage stage) {
		try {
			FXMLLoader loadFrame = new FXMLLoader(Chess.class.getClassLoader().getResource("com/chess/resources/SettingsFrame.fxml"));
			Parent mainRoot = loadFrame.load();
			Scene scene = new Scene(mainRoot, 550, 630);
			scene.getStylesheets().add(Chess.class.getClassLoader().getResource("com/chess/resources/application.css").toExternalForm());
			if (stage == null) {
				stage = new Stage();
				stage.setMinWidth(566);
				stage.setMinHeight(669);
			}
			stage.setScene(scene); 
			stage.setTitle("Chess v0.8");
			controller = loadFrame.getController();
			controller.setStage(stage);
			controller.setMainAccess(this);
			scene.setFill(Color.TRANSPARENT);
			stage.getIcons().add(new Image("com/chess/resources/img/pawn_b.png"));
			stage.show();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Arrays.deepToString(e.getStackTrace()));
		}
	}
	
	@Override
	public void start(Stage args) {
		getSettingsScreen(null);
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		if (controller != null) {
			controller.handleExit(null); 
		}
		Platform.exit();
		System.exit(0);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
