package com.sterndu.viergewinnt;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.*;
import java.util.stream.Stream;
import com.sterndu.multicore.*;
import com.sterndu.multicore.MultiCore.TaskHandler;
import com.sterndu.util.interfaces.ThrowingConsumer;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

public class Game {

	// TODO new round; mp playable

	private final Window window;
	private final char[][] state;
	//max depth to check for falling
	private final byte[] maxvals;
	private ScanValue won;
	private boolean isFalling=false;
	private Mode mode;
	private com.sterndu.data.transfer.secure.Socket sock;
	private List<String> addresses;
	private TaskHandler serverTaskHandler;

	public Game(Window wind, Mode mode) {
		this.mode = mode;
		maxvals = new byte[7];
		for (int i = 0; i < maxvals.length; i++) maxvals[i] = 4;
		state = new char[7][6];
		for (int i = 0; i < state.length; i++)
			for (int j = 0; j < state[i].length; j++)
				state[i][j] = 'e';
		window = wind;
	}

	private void animation() {
		List<Pos> gaps;
		//find gaps and execute if found
		if ((gaps=gaps()).size()>0)
			for (Pos pos: gaps) try {
				//copy old to new location
				state[pos.x()][pos.y() + 1] = state[pos.x()][pos.y()];
				//remove old value
				state[pos.x()][pos.y()] = 'e';
				//get old img position
				ImageView imgViewFrom = (ImageView) window.getClass().getMethod("getImg" + pos.y() + pos.x())
						.invoke(window);
				//get new position
				ImageView imgViewTo = (ImageView) window.getClass()
						.getMethod("getImg" + (pos.y() + 1) + pos.x()).invoke(window);
				//copy img from old to new position
				imgViewTo.setImage(imgViewFrom.getImage());
				//set img on old position to empty
				imgViewFrom.setImage(Window.IMG_EMPTY);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		else {
			isFalling = false;
			checkWin();
		}
	}

	private void checkWin() {
		//check if won
		boolean isWon = won != null;
		if (System.getProperty("debug").equals("true"))
			System.out.println(won == null ? Arrays.deepToString(state) : won);
		// only execute if not yet won and no animation is in progress
		if (won == null && !isFalling) {
			Byte[] s_arr = new Byte[state.length];
			for (byte i = 0; i < s_arr.length; i++) s_arr[i] = i;
			// map to boolean -> filter all false's out -> count all true's
			if (Stream.of(s_arr).map(this::isValidMove).filter(b -> b).count() < 1) { // checks if all columns are full
				won = new ScanValue('e', (byte) 0, (byte) 0); // theres no winner
				window.getTextfield().setText("Unentschieden!");
				return;
			}
			// create an array able to contain all horizontal lines and all diagonal lines
			ScanValue[] scans = new ScanValue[state.length + (state.length + state[0].length - 7) * 2];
			for (int i = 0; i < state.length; i++) {
				char[] column = state[i];
				int dlines = state.length + column.length - 7; // calculate diagonal lines
				for (int j = 0; j < column.length; j++) {
					int d1LocalIdx = i + j - 3; // first diagonal index
					int d2LocalIdx = state.length - i + j - 4;// second diagonal index
					int horizontal_idx = j + 1;
					// very important to get if an index is out of bounds
					int diagonal1_idx = d1LocalIdx < 0 || d1LocalIdx >= dlines ? -1 : column.length + 1 + d1LocalIdx;
					int diagonal2_idx = d2LocalIdx < 0 || d2LocalIdx >= dlines ? -1
							: column.length + dlines + 1 + d2LocalIdx;
					if (column[j] != 'e') {// check if not empty
						if (scans[0] != null) {// if we are in a scan line
							if (scans[0].getColor() == column[j])// if scan line is of same color e.g. Red or Yellow
								scans[0].appendPosition((byte) i, (byte) j);
							else {
								if (scans[0].getLength() >= 4) break;// if the scan line is of length 4 or more
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
						if (scans[0] != null) scans[0] = null;// reset scan lines
						if (scans[horizontal_idx] != null) scans[horizontal_idx] = null;
						if (diagonal1_idx != -1) if (scans[diagonal1_idx] != null) scans[diagonal1_idx] = null;
						if (diagonal2_idx != -1) if (scans[diagonal2_idx] != null) scans[diagonal2_idx] = null;
					}
				}
				if (System.getProperty("debug").equals("true"))
					System.out.println(i + " : " + Arrays.toString(scans));
				try {// check for the first scan line of 4 or more
					for (ScanValue sv: scans) if (sv != null) if (sv.getLength() >= 4 && won == null) won = sv;
				} catch (Exception e) {
					e.printStackTrace();
				}
				scans[0] = null;// erase the memory!!!! (to prevent an issue of roll over)
			}
		}
		if (won != null != isWon && !isWon) {
			if (window.isRed_turn() != (won.getColor() == 'r') || window.isRed_turn() == (won.getColor() == 'y'))
				window.turn();
			// print that someone won
			window.getTextfield().setText(won.getColor() == 'y' ? "Gelb hat Gewonnen!" : "Rot hat Gewonnen!");
			try {
				ImageView startField = (ImageView) window.getClass()
						.getMethod("getImg" + won.getPositions().get(0).y() + won.getPositions().get(0).x())
						.invoke(window);
				ImageView endField = (ImageView) window.getClass()
						.getMethod("getImg" + won.getPositions().get(3).y() + won.getPositions().get(3).x())
						.invoke(window);
				window.getLine().setStartX(startField.getLayoutX() + startField.getFitWidth() / 2);
				window.getLine().setStartY(startField.getLayoutY() + startField.getFitHeight() / 2);
				window.getLine().setEndX(endField.getLayoutX() + endField.getFitWidth() / 2);
				window.getLine().setEndY(endField.getLayoutY() + endField.getFitHeight() / 2);
				window.getLine().setVisible(true);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
	}

	private void doAiMove() {
		if (won == null && !isFalling) try {

			byte x = 0;
			state[x][0] = window.isRed_turn() ? 'r' : 'y';
			ImageView field = (ImageView) window.getClass().getMethod("getImg0" + x).invoke(window);
			field.setImage(window.isRed_turn() ? Window.IMG_RED : Window.IMG_YELLOW);
			window.turn();
			isFalling = true;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			e.printStackTrace();
		}
	}

	private List<Pos> gaps() {
		List<Pos> li = new ArrayList<>();
		if (isFalling) for (int i = 0; i < state.length; i++) {
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
		return li;

	}

	private void handleRecievedMove(byte type, byte[] data) {
		if (data.length > 0) {
			byte x = data[0];
			System.out.println(x + " " + sock.isHost() + " " + window.isRed_turn());
			if (sock.isHost() ? window.isRed_turn() : !window.isRed_turn()) try {
				ImageView field = (ImageView) window.getClass().getMethod("getImg0" + x).invoke(window);
				state[x][0] = window.isRed_turn() ? 'r' : 'y';
				field.setImage(window.isRed_turn() ? Window.IMG_RED : Window.IMG_YELLOW);
				isFalling = true;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				new InvalidMoveException(x + "|0").printStackTrace();
			}
		}
	}

	private void handleRecievedReset(byte type, byte[] data) {
		System.out.println(type);
		reset(mode, false);
	}

	private void handleRecievedTurn(byte type, byte[] data) {
		window.turn();
	}

	public void close() {
		if (System.getProperty("debug").equals("true"))
			System.out.println("rem");
		Updater u = Updater.getInstance();
		u.remove("animation");
		u.remove("ai move");
		if (serverTaskHandler != null) {
			MultiCore.removeTaskHandler(serverTaskHandler);
			serverTaskHandler = null;
		}
		try {
			if (sock != null) {
				if (!sock.isClosed()) {
					sock.sendClose();
					sock.close();
				}
				sock = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Mode getMode() {
		return mode;
	}

	public List<Pos> getPossibleMoves() {
		List<Pos> li = new ArrayList<>();
		for (byte i = 0; i < state.length; i++)
			if (isValidMove(i)) li.add(new Pos(i, (byte) (maxvals[i] + 1)));
		return li;
	}

	public boolean isValidMove(byte x) {
		if (System.getProperty("debug").equals("true"))
			System.out.println(x);
		return state[x][0] == 'e';// checks if top row field is empty
	}

	public void move(byte x) throws InvalidMoveException {
		System.out.println(x);
		if (System.getProperty("debug").equals("true"))
			System.out.println(gaps().size() + " " + isFalling);
		if (gaps().size() == 0 && isValidMove(x) && won == null)
			if (getMode() == Mode.LOCAL || getMode() == Mode.AI && !window.isRed_turn()) {
				if (System.getProperty("debug").equals("true"))
					System.out.println(getPossibleMoves());
				try {
					ImageView field = (ImageView) window.getClass().getMethod("getImg0" + x).invoke(window);
					state[x][0] = window.isRed_turn() ? 'r' : 'y';
					field.setImage(window.isRed_turn() ? Window.IMG_RED : Window.IMG_YELLOW);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
					throw new InvalidMoveException(x + "|0");
				}
				window.turn();
				isFalling = true;
			} else if (getMode() == Mode.ONLINE && sock != null && sock.isConnected() && !sock.isClosed()) {
				if (!window.isRed_turn()) try {
					ImageView field = (ImageView) window.getClass().getMethod("getImg0" + x).invoke(window);
					sock.sendData((byte) 2, new byte[] {x});
					state[x][0] = 'y';
					field.setImage(Window.IMG_YELLOW);
					sock.sendData((byte) 1, new byte[0]);
					window.turn();
					isFalling = true;
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException | SocketException e) {
					e.printStackTrace();
					throw new InvalidMoveException(x + "|0");
				}
			} else if (getMode() == Mode.AI) {

			} else if (getMode() == Mode.JOIN && sock != null && sock.isConnected() && !sock.isClosed()
					&& window.isRed_turn())
				try {
					ImageView field = (ImageView) window.getClass().getMethod("getImg0" + x).invoke(window);
					sock.sendData((byte) 2, new byte[] {x});
					state[x][0] = 'r';
					field.setImage(Window.IMG_RED);
					window.turn();
					sock.sendData((byte) 1, new byte[0]);
					isFalling = true;
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException | SocketException e) {
					e.printStackTrace();
					throw new InvalidMoveException(x + "|0");
				}
	}

	public void reset(Mode mode, boolean send) {
		if (sock != null && sock.isConnected() && !sock.isClosed() && send) try {
			sock.sendData((byte) 3, new byte[0]);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		window.getLine().setVisible(false);
		window.turn();
		window.turn();
		won = null;
		this.mode = mode;
		Updater u = Updater.getInstance();
		u.add((Runnable) this::animation, "animation", 100);
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
		if (mode == Mode.ONLINE) {
			addresses = new ArrayList<>();
			try {
				addresses.add(InetAddress.getLocalHost().getHostAddress());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			try {
				final URL whatismyip = new URL("https://ipv4.wtfismyip.com/text");
				final BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
				addresses.add(in.readLine());
			} catch (final ConnectException e) {
				try {
					final URL whatismyip1 = new URL("https://ipv4.myip.addr.space/ip");
					final BufferedReader in1 = new BufferedReader(new InputStreamReader(whatismyip1.openStream()));
					addresses.add(in1.readLine());
				} catch (final IOException e3) {
					e3.printStackTrace();
				}
			} catch (final IOException e2) {
				e2.printStackTrace();
			}
			try {
				final URL whatismyip6 = new URL("https://ipv6.wtfismyip.com/text");
				final BufferedReader in6 = new BufferedReader(new InputStreamReader(whatismyip6.openStream()));
				addresses.add(in6.readLine());
			} catch (final Exception e) {
				try {
					final URL whatismyip61 = new URL("https://ipv6.myip.addr.space/ip");
					final BufferedReader in61 = new BufferedReader(new InputStreamReader(whatismyip61.openStream()));
					addresses.add(in61.readLine());
				} catch (final Exception e3) {
					e3.printStackTrace();
				}
			}
			Game theGame = this;
			MultiCore.addTaskHandler(serverTaskHandler = new TaskHandler(1000) {

				private boolean isInTask = false;

				private void setPortaAddress(int port) {
					StringBuilder sb = new StringBuilder();
					for (String address: addresses) sb.append(address + ":" + port + "\n");
					window.getAddressField().setText(sb.toString().substring(0, sb.length() - 1));
				}

				@Override
				protected ThrowingConsumer<TaskHandler> getTask() {
					if (isInTask || sock != null && !sock.isClosed() && sock.isConnected()) return th -> {};
					else {
						isInTask = true;
						return th -> {
							try (com.sterndu.data.transfer.secure.ServerSocket server = new com.sterndu.data.transfer.secure.ServerSocket(
									0)) {
								setPortaAddress(server.getLocalPort());
								window.getAddressField().setVisible(true);
								com.sterndu.data.transfer.secure.Socket s = server.accept();
								sock = s;
								sock.setHandle((byte) 1, theGame::handleRecievedTurn);
								sock.setHandle((byte) 2, theGame::handleRecievedMove);
								sock.setHandle((byte) 3, theGame::handleRecievedReset);
								System.out.println(sock.isConnected());
								if (sock.isConnected()) window.getAddressField().setVisible(false);
								isInTask = false;
							} catch (IOException e) {
								e.printStackTrace();
							}
						};
					}
				}

				@Override
				protected boolean hasTask() {
					return (sock == null || sock != null && (!sock.isConnected() || sock.isClosed())) && !isInTask;
				}
			});
		} else if (mode == Mode.JOIN) {
			if (sock == null || !sock.isConnected() || sock.isClosed()) {
				window.getJoinInput().setVisible(true);
				window.getJoinInput().setOnKeyReleased(ke -> {
					if (ke.getCode() == KeyCode.ENTER) {
						String text = window.getJoinInput().getText();
						if (text.contains(":")) {
							int lcolon = text.lastIndexOf(':');
							String port = text.substring(lcolon + 1);
							String addr = text.substring(0, lcolon);
							try {
								int i_port = Integer.parseInt(port);
								if (i_port <=65535&&i_port >=0) {
									InetSocketAddress address=new InetSocketAddress(addr, i_port);
									System.out.println(address + " " + !address.isUnresolved());
									if (!address.isUnresolved()) try {
										sock = new com.sterndu.data.transfer.secure.Socket(addr, i_port);
										sock.setShutdownHook(s -> {
											window.getJoinInput().setVisible(true);
										});
										sock.setHandle((byte) 1, this::handleRecievedTurn);
										sock.setHandle((byte) 2, this::handleRecievedMove);
										sock.setHandle((byte) 3, this::handleRecievedReset);
										window.getJoinInput().setVisible(false);
										window.getJoinInput().setText("");
										System.out.println(sock);
									} catch (IOException e) {
										window.getJoinInput().setText("Can't connect to: "+text);
									}
								} else window.getJoinInput().setText(port + " is out of range! [0...65535] " + text);
							} catch (NumberFormatException e) {
								window.getJoinInput().setText(port + " is not a valid port! " + text);
							}
							// TODO
						} else window.getJoinInput().setText("Wasn't able to find a port in address: " + text);
					}
				});
			}
		} else if (mode == Mode.AI) {
			u.add((Runnable) this::doAiMove, "ai move", 100);
			// TODO
			this.mode = mode;
		}
	}

}
