package com.sterndu.viergewinnt;

import java.util.*;
import com.sterndu.multicore.Updater;

public class Game {

	private final List<Object> watched=new ArrayList<>();
	private final Window window;

	public Game(Window wind) {
		window = wind;
		Updater u = Updater.getInstance();
		u.add((Runnable) this::checkWin, "check for win", 10);
		u.add((Runnable) this::animation, "animation", 200);
	}

	private void animation() {

	}

	private void checkWin() {

	}

	public boolean isValidMove(String field) {
		System.out.println(Integer.parseInt(field.substring(4)));
		return false;
	}

	public void move(String field) {

	}

}
