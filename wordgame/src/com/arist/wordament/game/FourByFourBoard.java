package com.arist.wordament.game;

public class FourByFourBoard extends Board {
	private static int SIZE = 16;
	private static int WIDTH = 4; 

	private static final int transitionBits[] = {
		0x32,0x75,0xea,0xc4,
		0x323,0x757,0xeae,0xc4c,
		0x3230,0x7570,0xeae0,0xc4c0,
		0x2300,0x5700,0xae00,0x4c00
	};

	public FourByFourBoard(String[] b) {
		super(b);
	}

	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public int getWidth() {
		return WIDTH;
	}

	@Override
	public int transitions(int position) {
		return transitionBits[position];
	}

}
