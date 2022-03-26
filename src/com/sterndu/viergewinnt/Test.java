package com.sterndu.viergewinnt;

public class Test {

	public static void main(String[] args) {
		int w = 7, h = 6;
		int dlines = w + h - 7;
		System.out.println(dlines);
		int[][] i_arr = new int[dlines * 2][Math.min(h, w)];
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int val = i + j - 3;
				System.out.print((val < 0 || val >= dlines ? " " : val) + " ");
			}
			System.out.println();
		}
		System.out.println();
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int val = w - i + j - 4;
				System.out.print((val < 0 || val >= dlines ? " " : val) + " ");
			}
			System.out.println();
		}
	}

}
