package com.sterndu.viergewinnt;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import com.sterndu.multicore.Updater;
import javafx.scene.image.ImageView;

public class Game {

	private final List<byte[]> watched = new ArrayList<>();
	private final Window window;
	private final char[][] state;

	public Game(Window wind) {
		state = new char[7][6];
		for (int i = 0; i < state.length; i++)
			for (int j = 0; j < state[i].length; j++)
				state[i][j] = 'e';
		window = wind;
		Updater u = Updater.getInstance();
		u.add((Runnable) this::checkWin, "check for win", 10);
		u.add((Runnable) this::animation, "animation", 200);
	}

	private void animation() {
		List<Integer> remove = new ArrayList<>();
		for (int i = 0; i < watched.size(); i++) {
			byte[] b_arr = watched.get(i);
			char[] column = state[b_arr[0]];
			if (b_arr[1] >= 5) {
				remove.add(i);
				continue;
			}
			if (column[b_arr[1] + 1] != 'e') remove.add(i);
			else {
				column[b_arr[1] + 1] = column[b_arr[1]];
				column[b_arr[1]] = 'e';
				try {
					ImageView imgViewFrom = (ImageView) window.getClass().getMethod("getImg" + b_arr[1] + b_arr[0])
							.invoke(window);
					ImageView imgViewTo = (ImageView) window.getClass()
							.getMethod("getImg" + (++b_arr[1]) + b_arr[0]).invoke(window);
					imgViewTo.setImage(imgViewFrom.getImage());
					imgViewFrom.setImage(Window.IMG_EMPTY);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
		}
		for (Integer idx: remove)
			watched.remove(idx);
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
		if (isValidMove(field)) {
			byte x = Byte.parseByte(field.substring(4));
			state[x][0] = window.isRed_turn() ? 'r' : 'y';
			watched.add(new byte[] {x, 0});
		} else throw new InvalidMoveException(field.substring(3));
	}

}
