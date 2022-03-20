package com.sterndu.viergewinnt;

import java.util.*;
import com.sterndu.multicore.Updater;

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
		//TODO
	}

	private void checkWin() {
		//TODO
	}

	public void close() {
		Updater u = Updater.getInstance();
		u.remove("check for win");
		u.remove("animation");
	}

	public boolean isValidMove(String field) {
		System.err.println(Integer.parseInt(field.substring(4)));
		return state[Integer.parseInt(field.substring(4))][0] == 'e';
	}

	public void move(String field) throws InvalidMoveException {
		if (isValidMove(field)) {
			// TODO
		} else throw new InvalidMoveException(field.substring(3));
	}

}
