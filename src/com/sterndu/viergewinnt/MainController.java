package com.sterndu.viergewinnt;

import java.io.IOException;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainController {

	@FXML
	private Button local, online, ai, join;

	private FXMLLoader menu_loader, window_loader;
	private Stage primaryStage;
	private Scene me, gameScene;
	private Game game;

	@FXML
	private void onAi() {
		if (System.getProperty("debug").equals("true"))
			System.out.println("Ai");
		setGame(Mode.AI);
	}

	@FXML
	private void onJoin() {
		if (System.getProperty("debug").equals("true"))
			System.out.println("Join");
		setGame(Mode.JOIN);
	}

	@FXML
	private void onLocal() {
		if (System.getProperty("debug").equals("true"))
			System.out.println("Local");
		setGame(Mode.LOCAL);
	}

	@FXML
	private void onOnline() {
		if (System.getProperty("debug").equals("true"))
			System.out.println("Online");
		setGame(Mode.ONLINE);
	}

	private void setGame(Mode mode) {
		game.reset(mode, true);
		primaryStage.setScene(gameScene);
	}


	public void init(FXMLLoader menu_loader, FXMLLoader window_loader, Stage primaryStage) {
		if (this.menu_loader == null)
			this.menu_loader = menu_loader;
		if (this.window_loader == null)
			this.window_loader = window_loader;
		if (this.primaryStage == null)
			this.primaryStage = primaryStage;
		if (me == null)
			me = primaryStage.getScene();
		try {
			Pane root = window_loader.load();
			Window controller = window_loader.getController();
			root.getChildren().add(controller.getLine());
			gameScene = new Scene(root);
			controller.init(game = new Game(controller, Mode.LOCAL), this);
			controller.setStyle();
			primaryStage.setOnCloseRequest(we -> {
				controller.close();
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setMenu() {
		primaryStage.setScene(me);
		primaryStage.setOnCloseRequest(we -> {});
	}
}
