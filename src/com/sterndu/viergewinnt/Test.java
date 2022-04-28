package com.sterndu.viergewinnt;

import java.net.*;

public class Test {

	public static void main(String[] args) {
		int w = 7, h = 6;
		//calculate diagonal lines to check
		int dlines = w + h - 7;
		System.out.println(dlines);
		//create array
		int[][] i_arr = new int[dlines * 2][Math.min(h, w)];
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				//calculate index
				int val = i + j - 3;
				System.out.print((val < 0 || val >= dlines ? " " : val) + " ");
			}
			System.out.println();
		}
		System.out.println();
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				//calculate index
				int val = w - i + j - 4;
				System.out.print((val < 0 || val >= dlines ? " " : val) + " ");
			}
			System.out.println();
		}
	}

}
