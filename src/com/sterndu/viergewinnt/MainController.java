package com.sterndu.viergewinnt;

import java.io.IOException;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainController {

	@FXML
	private Button local, online, ai;

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
		game.reset(mode);
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
			Parent root = window_loader.load();
			Window controller = window_loader.getController();
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
