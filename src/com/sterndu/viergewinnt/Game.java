package com.sterndu.viergewinnt;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import com.sterndu.multicore.Updater;
import javafx.scene.image.ImageView;

public class Game {

	private final Window window;
	private final char[][] state;
	private final byte[] maxvals;
	private ScanValue won;
	private boolean isFalling=false;
	private Mode mode;

	public Game(Window wind, Mode mode) {
		this.mode = mode;
		maxvals = new byte[7];
		for (int i = 0; i < maxvals.length; i++) maxvals[i] = 4;
		state = new char[7][6];
		for (int i = 0; i < state.length; i++)
			for (int j = 0; j < state[i].length; j++)
				state[i][j] = 'e';
		window = wind;
		Updater u = Updater.getInstance();
		u.add((Runnable) this::checkWin, "check for win", 100);
		u.add((Runnable) this::animation, "animation", 100);
	}

	private void animation() {
		List<Pos> gaps;
		if ((gaps=gaps()).size()>0)
			for (Pos pos: gaps) try {
				state[pos.x()][pos.y() + 1] = state[pos.x()][pos.y()];
				state[pos.x()][pos.y()] = 'e';
				ImageView imgViewFrom = (ImageView) window.getClass().getMethod("getImg" + pos.y() + pos.x())
						.invoke(window);
				ImageView imgViewTo = (ImageView) window.getClass()
						.getMethod("getImg" + (pos.y() + 1) + pos.x()).invoke(window);
				imgViewTo.setImage(imgViewFrom.getImage());
				imgViewFrom.setImage(Window.IMG_EMPTY);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
	}

	private void checkWin() {
		boolean isWon = won != null;
		if (System.getProperty("debug").equals("true"))
			System.out.println(won == null ? Arrays.deepToString(state) : won);
		if (won == null && !isFalling) {
			ScanValue[] scans = new ScanValue[state.length + (state.length + state[0].length - 7) * 2];
			for (int i = 0; i < state.length; i++) {
				char[] column = state[i];
				for (int j = 0; j < column.length; j++) {
					int dlines = state.length + column.length - 7;
					int d1LocalIdx = i + j - 3;
					int d2LocalIdx = state.length - i + j - 4;
					int horizontal_idx = j + 1;
					int diagonal1_idx = d1LocalIdx < 0 || d1LocalIdx >= dlines ? -1 : column.length + 1 + d1LocalIdx;
					int diagonal2_idx = d2LocalIdx < 0 || d2LocalIdx >= dlines ? -1
							: column.length + dlines + 1 + d2LocalIdx;
					if (column[j] != 'e') {
						if (scans[0] != null) {
							if (scans[0].getColor() == column[j])
								scans[0].appendPosition((byte) i, (byte) j);
							else {
								if (scans[0].getLength() >= 4) break;
								scans[0].setColor(column[j], (byte) i, (byte) j);
							}
						} else scans[0] = new ScanValue(column[j], (byte) i, (byte) j);
						if (scans[horizontal_idx] != null) {
							if (scans[horizontal_idx].getColor() == column[j])
								scans[horizontal_idx].appendPosition((byte) i, (byte) j);
							else {
								if (scans[horizontal_idx].getLength() >= 4) break;
								scans[horizontal_idx].setColor(column[j], (byte) i, (byte) j);
							}
						} else scans[horizontal_idx] = new ScanValue(column[j], (byte) i, (byte) j);
						if (diagonal1_idx != -1) if (scans[diagonal1_idx] != null) {
							if (scans[diagonal1_idx].getColor() == column[j])
								scans[diagonal1_idx].appendPosition((byte) i, (byte) j);
							else {
								if (scans[diagonal1_idx].getLength() >= 4) break;
								scans[diagonal1_idx].setColor(column[j], (byte) i, (byte) j);
							}
						} else scans[diagonal1_idx] = new ScanValue(column[j], (byte) i, (byte) j);
						if (diagonal2_idx != -1) if (scans[diagonal2_idx] != null) {
							if (scans[diagonal2_idx].getColor() == column[j])
								scans[diagonal2_idx].appendPosition((byte) i, (byte) j);
							else {
								if (scans[diagonal2_idx].getLength() >= 4) break;
								scans[diagonal2_idx].setColor(column[j], (byte) i, (byte) j);
							}
						} else scans[diagonal2_idx] = new ScanValue(column[j], (byte) i, (byte) j);
					} else {
						if (scans[0] != null) scans[0] = null;
						if (scans[horizontal_idx] != null) scans[horizontal_idx] = null;
						if (diagonal1_idx != -1) if (scans[diagonal1_idx] != null) scans[diagonal1_idx] = null;
						if (diagonal2_idx != -1) if (scans[diagonal2_idx] != null) scans[diagonal2_idx] = null;
					}
				}
				if (System.getProperty("debug").equals("true"))
					System.out.println(i + " : " + Arrays.toString(scans));
				try {
					for (ScanValue sv: scans) if (sv != null) if (sv.getLength() >= 4 && won == null) won = sv;
				} catch (Exception e) {
					e.printStackTrace();
				}
				scans[0]=null;
			}
		}
		if (won != null != isWon && !isWon) {
			window.turn();
			window.getTextfield().setText(won.getColor() == 'y' ? "Gelb hat Gewonnen!" : "Rot hat Gewonnen!");
		}
	}

	private List<Pos> gaps() {
		List<Pos> li = new ArrayList<>();
		if (isFalling) {
			for (int i = 0; i < state.length; i++) {
				char[] column = state[i];
				// System.out.println(Arrays.toString(column));
				boolean empty = column[maxvals[i] + 1] == 'e';
				// System.out.println("idx:" + i + " maxv:" + maxvals[i] + " empty:" + empty);
				for (int j = maxvals[i]; j >= 0; j--) if (empty) {
					empty = true;
					if (column[j] != 'e')
						li.add(new Pos((byte) i, (byte) j));
				} else {
					if (j == maxvals[i]) maxvals[i]--;
					empty = column[j] == 'e';
				}
			}
			if (li.size() == 0)
				isFalling = false;
		}
		return li;

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

	public void move(ImageView field) throws InvalidMoveException {
		if (System.getProperty("debug").equals("true"))
			System.out.println(gaps().size() + " " + isFalling);
		if (gaps().size() == 0 && isValidMove(field.getId()) && won == null)
			if (mode == Mode.LOCAL || mode == Mode.AI && !window.isRed_turn()) {
				byte x = Byte.parseByte(field.getId().substring(4));
				state[x][0] = window.isRed_turn() ? 'r' : 'y';
				field.setImage(window.isRed_turn() ? Window.IMG_RED : Window.IMG_YELLOW);
				window.turn();
				isFalling = true;
			} else if (mode == Mode.ONLINE) {

			} else if (mode == Mode.AI) {

			}
	}

	public void reset(Mode mode) {
		window.turn();
		window.turn();
		won = null;
		this.mode = mode;
		if (window.isRed_turn()) window.turn();
		for (int i = 0; i < maxvals.length; i++) maxvals[i] = 4;
		for (int i = 0; i < state.length; i++)
			for (int j = 0; j < state[i].length; j++) {
				state[i][j] = 'e';
				try {
					ImageView imgView = (ImageView) window.getClass().getMethod("getImg" + j + i)
							.invoke(window);
					imgView.setImage(Window.IMG_EMPTY);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
	}

}
