package com.sterndu.viergewinnt;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import com.sterndu.multicore.Updater;
import javafx.scene.image.ImageView;

public class Game {

	private final Window window;
	private final char[][] state;
	private final byte[] maxvals;

	public Game(Window wind) {
		maxvals = new byte[7];
		for (int i = 0; i < maxvals.length; i++) maxvals[i] = 4;
		state = new char[7][6];
		for (int i = 0; i < state.length; i++)
			for (int j = 0; j < state[i].length; j++)
				state[i][j] = 'e';
		window = wind;
		Updater u = Updater.getInstance();
		u.add((Runnable) this::checkWin, "check for win", 800);
		u.add((Runnable) this::animation, "animation", 250);
	}

	private void animation() {
		synchronized (state) {
			for (int i = 0; i < state.length; i++) {
				char[] column = state[i];
				// System.out.println(Arrays.toString(column));
				boolean empty = column[maxvals[i] + 1] == 'e';
				System.out.println("idx:" + i + " maxv:" + maxvals[i] + " empty:" + empty);
				for (int j = maxvals[i]; j >= 0; j--) if (empty) {
					System.out.println(j);
					column[j + 1] = column[j];
					column[j] = 'e';
					empty = true;
					try {
						ImageView imgViewFrom = (ImageView) window.getClass().getMethod("getImg" + j + i)
								.invoke(window);
						ImageView imgViewTo = (ImageView) window.getClass()
								.getMethod("getImg" + (j + 1) + i).invoke(window);
						imgViewTo.setImage(imgViewFrom.getImage());
						imgViewFrom.setImage(Window.IMG_EMPTY);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
				} else {
					if (j == maxvals[i]) maxvals[i]--;
					empty = column[j] == 'e';
				}
			}
		}
	}

	private void checkWin() {
		System.out.println(Arrays.deepToString(state));
		//TODO
	}

	public void close() {
		Updater u = Updater.getInstance();
		u.remove("check for win");
		u.remove("animation");
	}

	public boolean isValidMove(String field) {
		if (System.getProperty("debug").equals("true"))
			System.out.println(Integer.parseInt(field.substring(4)));
		return state[Integer.parseInt(field.substring(4))][0] == 'e';
	}

	public void move(String field) throws InvalidMoveException {
		if (isValidMove(field))
			synchronized (state) {
				byte x = Byte.parseByte(field.substring(4));
				state[x][0] = window.isRed_turn() ? 'r' : 'y';
			}
		else throw new InvalidMoveException(field.substring(3));
	}

}
