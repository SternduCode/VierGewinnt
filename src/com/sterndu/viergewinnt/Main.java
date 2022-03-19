package com.sterndu.viergewinnt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Window.fxml"));
			Parent root = loader.load();
			Window controller = loader.getController();
			controller.setGame(new Game(controller));
			controller.setStyle();
			Scene scene = new Scene(root);
			primaryStage.setResizable(false);
			// controller.setStyle();
			// controller.getImg().setVisible(true);
			primaryStage.setTitle("Vier Gewinnt");
			primaryStage.setScene(scene);
			primaryStage.show();
			System.out.println(controller);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
