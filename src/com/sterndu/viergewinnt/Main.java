package com.sterndu.viergewinnt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		System.setProperty("debug", "false");
		launch(args);
		System.exit(0);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Menu.fxml"));
			FXMLLoader window_loader = new FXMLLoader(getClass().getResource("/resources/Window.fxml"));
			Parent root = loader.load();
			MainController controller = loader.getController();
			Scene scene = new Scene(root);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Vier Gewinnt");
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/resources/Connect4.png")));
			controller.init(loader, window_loader, primaryStage);
			primaryStage.show();
			if (System.getProperty("debug").equals("true"))
				System.out.println(controller);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


}
