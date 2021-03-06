package com.sterndu.viergewinnt;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Window {

	//image constats;
	public static final Image IMG_EMPTY, IMG_YELLOW, IMG_RED;

	//initializing image constants;
	static {
		IMG_EMPTY = new Image(Window.class.getResourceAsStream("/resources/paint.png"));
		IMG_YELLOW = new Image(Window.class.getResourceAsStream("/resources/paintYellow.png"));
		IMG_RED = new Image(Window.class.getResourceAsStream("/resources/paintRed.png"));
	}

	//handles rules and game state;
	private Game game;

	// whose turn it is;
	private boolean red_turn;

	private MainController main;

	@FXML
	private Button back;

	private final Line line;

	@FXML
	private TextField textfield;

	@FXML
	private TextField joinInput;

	@FXML
	private TextArea addressField;

	@FXML
	private ImageView img00, img01, img02, img03, img04, img05, img06,
	img10, img11, img12, img13, img14, img15, img16,
	img20, img21, img22, img23, img24, img25, img26,
	img30, img31, img32, img33, img34, img35, img36,
	img40, img41, img42, img43, img44, img45, img46,
	img50, img51, img52, img53, img54, img55, img56;

	public Window() {
		red_turn = false;
		line = new Line(0, 0, 0, 0);
		line.setStrokeWidth(4);
		line.setStroke(Color.color(.3, .3, .3, .8));
		line.setVisible(false);
	}

	@FXML
	private void click(MouseEvent event) {
		if (game == null) {
			textfield.setText("Etwas ist schief gelaufen!");
			return;
		}
		try {
			game.move(Byte.parseByte(((ImageView) event.getSource()).getId().substring(4)));
		} catch (InvalidMoveException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void type(KeyEvent event) {
		if (event.getCode().isDigitKey()) {
			int v = Character.digit(event.getText().charAt(0), 10);
			if (v > 0 && v < 8) {
				if (game == null) {
					textfield.setText("Etwas ist schief gelaufen!");
					return;
				}
				try {
					game.move((byte) (v - 1));
				} catch (InvalidMoveException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void close() {
		if (System.getProperty("debug").equals("true"))
			System.out.println("Win close " + game);
		if (game != null)
			game.close();
	}

	public TextArea getAddressField() { return addressField; }

	public Button getBack() { return back; }

	public Game getGame() { return game; }

	public ImageView getImg00() { return img00; }

	public ImageView getImg01() { return img01; }

	public ImageView getImg02() { return img02; }

	public ImageView getImg03() { return img03; }

	public ImageView getImg04() { return img04; }

	public ImageView getImg05() { return img05; }

	public ImageView getImg06() { return img06; }

	public ImageView getImg10() { return img10; }

	public ImageView getImg11() { return img11; }

	public ImageView getImg12() { return img12; }

	public ImageView getImg13() { return img13; }

	public ImageView getImg14() { return img14; }

	public ImageView getImg15() { return img15; }

	public ImageView getImg16() { return img16; }

	public ImageView getImg20() { return img20; }

	public ImageView getImg21() { return img21; }

	public ImageView getImg22() { return img22; }

	public ImageView getImg23() { return img23; }

	public ImageView getImg24() { return img24; }

	public ImageView getImg25() { return img25; }

	public ImageView getImg26() { return img26; }

	public ImageView getImg30() { return img30; }

	public ImageView getImg31() { return img31; }

	public ImageView getImg32() { return img32; }

	public ImageView getImg33() { return img33; }

	public ImageView getImg34() { return img34; }

	public ImageView getImg35() { return img35; }

	public ImageView getImg36() { return img36; }

	public ImageView getImg40() { return img40; }

	public ImageView getImg41() { return img41; }

	public ImageView getImg42() { return img42; }

	public ImageView getImg43() { return img43; }

	public ImageView getImg44() { return img44; }

	public ImageView getImg45() { return img45; }

	public ImageView getImg46() { return img46; }

	public ImageView getImg50() { return img50; }

	public ImageView getImg51() { return img51; }

	public ImageView getImg52() { return img52; }

	public ImageView getImg53() { return img53; }

	public ImageView getImg54() { return img54; }

	public ImageView getImg55() { return img55; }

	public ImageView getImg56() { return img56; }

	public TextField getJoinInput() { return joinInput; }

	public Line getLine() { return line; }

	public TextField getTextfield() { return textfield; }

	public void init(Game game, MainController main) {
		if (this.game==null)
			this.game=game;
		if (this.main==null)
			this.main=main;
	}

	public boolean isRed_turn() {
		return red_turn;
	}

	public void newRound() {
		if (game != null) game.reset(game.getMode(), true);
	}

	public void onBack() {
		game.close();
		addressField.setVisible(false);
		joinInput.setVisible(false);
		main.setMenu();
	}

	public void setStyle() {
		try {
			if (red_turn) {
				textfield.setText("Rot ist dran");
				textfield.setStyle("-fx-highlight-fill: #e00000;"
						+ "-fx-highlight-text-fill: #d3d3d3;"
						+ "-fx-background-color: #e00000;"
						+ "-fx-text-fill: #d3d3d3;");
			} else {
				textfield.setText("Gelb ist dran");
				textfield.setStyle("-fx-highlight-fill: #fff200;"
						+ "-fx-highlight-text-fill: #777aaa;"
						+ "-fx-background-color: #fff200;"
						+ "-fx-text-fill: #777aaa;");
			}
		} catch (NullPointerException e) {

		}
	}

	@Override
	public String toString() {
		return "Window [textfield=" + textfield + ", img00=" + img00 + ", img01=" + img01 + ", img02="
				+ img02 + ", img03=" + img03 + ", img04=" + img04 + ", img05=" + img05 + ", img06="
				+ img06 + ", img10=" + img10 + ", img11=" + img11 + ", img12=" + img12 + ", img13="
				+ img13 + ", img14=" + img14 + ", img15=" + img15 + ", img16=" + img16 + ", img20="
				+ img20 + ", img21=" + img21 + ", img22=" + img22 + ", img23=" + img23 + ", img24="
				+ img24 + ", img25=" + img25 + ", img26=" + img26 + ", img30=" + img30 + ", img31="
				+ img31 + ", img32=" + img32 + ", img33=" + img33 + ", img34=" + img34 + ", img35="
				+ img35 + ", img36=" + img36 + ", img40=" + img40 + ", img41=" + img41 + ", img42="
				+ img42 + ", img43=" + img43 + ", img44=" + img44 + ", img45=" + img45 + ", img46="
				+ img46 + ", img50=" + img50 + ", img51=" + img51 + ", img52=" + img52 + ", img53="
				+ img53 + ", img54=" + img54 + ", img55=" + img55 + ", img56=" + img56 + "]";
	}

	public void turn() {
		red_turn = !red_turn;
		setStyle();
	}

}
