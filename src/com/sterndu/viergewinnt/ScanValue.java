package com.sterndu.viergewinnt;

import java.util.*;

public class ScanValue {
	private char color;
	private int length;
	private List<Pos> positions;

	ScanValue(char color, byte x, byte y) {
		setColor(color, x, y);
	}

	void appendPosition(byte x, byte y) {
		positions.add(new Pos(x, y));
		length++;
	}

	void setColor(char color,byte x,byte y) {
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