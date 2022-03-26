package com.sterndu.viergewinnt;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import com.sterndu.multicore.Updater;
import javafx.scene.image.ImageView;

public class Game {

	private static record Pos(byte x, byte y) {

	}

	private static class ScanValue {
		private char color;
		private int length;
		private List<Pos> positions;

		private ScanValue(char color, byte x, byte y) {
			setColor(color, x, y);
		}

		private void appendPosition(byte x, byte y) {
			positions.add(new Pos(x, y));
			length++;
		}

		private void setColor(char color,byte x,byte y) {
			this.color=color;
			length = 0;
			positions=new ArrayList<>();
			appendPosition(x, y);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			ScanValue other = (ScanValue) obj;
			return color == other.color && length == other.length
					&& Objects.equals(positions, other.positions);
		}

		public char getColor() { return color; }

		public int getLength() { return length; }

		public List<Pos> getPositions() { return new ArrayList<>(positions); }

		@Override
		public int hashCode() {
			return Objects.hash(color, length, positions);
		}

		@Override
		public String toString() {
			return "ScanValue [color=" + color + ", length=" + length + ", positions=" + positions + "]";
		}
	}

	private final Window window;
	private final char[][] state;
	private final byte[] maxvals;
	private ScanValue won;
	private boolean isFalling=false;

	public Game(Window wind) {
		maxvals = new byte[7];
		for (int i = 0; i < maxvals.length; i++) maxvals[i] = 4;
		state = new char[7][6];
		for (int i = 0; i < state.length; i++)
			for (int j = 0; j < state[i].length; j++)
				state[i][j] = 'e';
		window = wind;
		Updater u = Updater.getInstance();
		u.add((Runnable) this::checkWin, "check for win", 200);
		u.add((Runnable) this::animation, "animation", 100);
	}

	private void animation() {
		List<Pos> gaps;
		if ((gaps=gaps()).size()>0)
			for (Pos pos: gaps) try {
				state[pos.x][pos.y + 1] = state[pos.x][pos.y];
				state[pos.x][pos.y] = 'e';
				ImageView imgViewFrom = (ImageView) window.getClass().getMethod("getImg" + pos.y + pos.x)
						.invoke(window);
				ImageView imgViewTo = (ImageView) window.getClass()
						.getMethod("getImg" + (pos.y + 1) + pos.x).invoke(window);
				imgViewTo.setImage(imgViewFrom.getImage());
				imgViewFrom.setImage(Window.IMG_EMPTY);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
	}

	private void checkWin() {
		boolean isWon = won != null;
		System.out.println(won == null ? Arrays.deepToString(state) : won);
		if (won == null && !isFalling) {
			ScanValue[] scans = new ScanValue[state.length + (state.length + state[0].length - 7) * 2];
			for (int i = 0; i < state.length; i++) {
				char[] column = state[i];
				for (int j = 0; j < column.length; j++) {
					int dlines = state.length + column.length - 7,
							d1LocalIdx = i + j - 3,
							d2LocalIdx = state.length - i + j - 4;
					int horizontal_idx = j + 1,
							diagonal1_idx = d1LocalIdx < 0 || d1LocalIdx >= dlines ? -1 : column.length + 1 + d1LocalIdx,
									diagonal2_idx = d2LocalIdx < 0 || d2LocalIdx >= dlines ? -1 : column.length + dlines + 1 + d2LocalIdx;
									if (column[j] != 'e') {
										if (scans[0] != null) {
											if (scans[0].color == column[j]) scans[0].appendPosition((byte) i, (byte) j);
											else {
												if (scans[0].length >= 4) break;
												scans[0].setColor(column[j], (byte) i, (byte) j);
											}
										} else scans[0] = new ScanValue(column[j], (byte) i, (byte) j);
										if (scans[horizontal_idx] != null) {
											if (scans[horizontal_idx].color == column[j]) scans[horizontal_idx].appendPosition((byte) i, (byte) j);
											else {
												if (scans[horizontal_idx].length >= 4) break;
												scans[horizontal_idx].setColor(column[j], (byte) i, (byte) j);
											}
										} else scans[horizontal_idx] = new ScanValue(column[j], (byte) i, (byte) j);
										if (diagonal1_idx != -1) if (scans[diagonal1_idx] != null) {
											if (scans[diagonal1_idx].color == column[j]) scans[diagonal1_idx].appendPosition((byte) i, (byte) j);
											else {
												if (scans[diagonal1_idx].length >= 4) break;
												scans[diagonal1_idx].setColor(column[j], (byte) i, (byte) j);
											}
										} else scans[diagonal1_idx] = new ScanValue(column[j], (byte) i, (byte) j);
										if (diagonal2_idx != -1) if (scans[diagonal2_idx] != null) {
											if (scans[diagonal2_idx].color == column[j]) scans[diagonal2_idx].appendPosition((byte) i, (byte) j);
											else {
												if (scans[diagonal2_idx].length >= 4) break;
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
				System.out.println(i + " : "+Arrays.toString(scans));
				try {
					for (ScanValue sv: scans) if (sv != null) if (sv.length >= 4 && won == null) won = sv;
				} catch (Exception e) {
					e.printStackTrace();
				}
				scans[0]=null;
			}
		}
		if (won != null != isWon && !isWon) {
			window.turn();
			window.getTextfield().setText(won.color == 'y' ? "Gelb hat Gewonnen!" : "Rot hat Gewonnen!");
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
		System.out.println(gaps().size() + " " + isFalling);
		if (gaps().size() == 0 && isValidMove(field.getId()) && won == null) {
			byte x = Byte.parseByte(field.getId().substring(4));
			state[x][0] = window.isRed_turn() ? 'r' : 'y';
			field.setImage(window.isRed_turn() ? Window.IMG_RED : Window.IMG_YELLOW);
			window.turn();
			isFalling = true;
		}
	}

}
