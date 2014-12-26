package com.arist.wordament.game;

import com.arist.trie.TransitionMap;
import com.arist.trie.Trie;

public abstract class Board implements TransitionMap {
	private String[] board;

	public Board(String[] b) {
		board = b;
	}

	public synchronized String elementAt(int i) {
		return board[i];
	}

	public synchronized String elementAt(int x,int y) {
		return board[x+getWidth()*y];
	}

	@Override
	public synchronized int valueAt(int i) {
		return Trie.ctoi(board[i].charAt(0));
	}

	@Override
	public synchronized String toString() {
		StringBuilder sb = new StringBuilder();
		int size = getSize();
		for(int i=0;i<size-1;i++) {
			sb.append(board[i]);
			sb.append(",");
		}
		sb.append(board[size-1]);

		return sb.toString();
	}

	@Override
	public abstract int getSize();
	public abstract int getWidth();

	@Override
	public abstract int transitions(int position);

}
