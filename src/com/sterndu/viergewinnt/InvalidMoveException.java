package com.sterndu.viergewinnt;

public class InvalidMoveException extends Exception {

	private static final long serialVersionUID = 8076737977289251094L;

	public InvalidMoveException(String target) {
		super("Can't execute move to "+target);
	}

}
