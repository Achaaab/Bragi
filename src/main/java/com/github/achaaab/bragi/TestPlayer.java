package com.github.achaaab.bragi;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.nio.file.Paths;

/**
 * Experiments with audio and JavaFX.
 *
 * @author Jonathan Guéhenneux
 * @since 0.1.2
 */
public class TestPlayer extends Application {

	/**
	 * @param arguments none
	 */
	public static void main(String... arguments) {
		launch();
	}

	@Override
	public void start(Stage stage) {

		var path = Paths.get("...");
		var media = new Media(path.toUri().toString());
		var player = new MediaPlayer(media);
		var view = new MediaView(player);

		var root = new Group(view);
		var scene = new Scene(root, 640, 360);

		stage.setTitle("Test MediaPlayer OpenJFX");
		stage.setScene(scene);
		stage.show();

		player.play();
	}
}