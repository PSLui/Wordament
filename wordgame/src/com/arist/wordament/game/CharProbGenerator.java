package com.arist.wordament.game;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class CharProbGenerator {
	private static final String TAG = "CharProbGenerator";
	private ArrayList<ProbabilityQueue> charProbs;

	public CharProbGenerator(InputStream letter_stream) {

		BufferedReader br = new BufferedReader(new InputStreamReader(
			letter_stream));

		charProbs = new ArrayList<ProbabilityQueue>();

		try {
			for(String line=br.readLine();line != null; line=br.readLine()) {
				String chunks[] = line.split(" ");
				ProbabilityQueue pq = new ProbabilityQueue(chunks[0]);
				for(int i=1;i<chunks.length;i++) {
					pq.addProb(chunks[i]);
				}
				charProbs.add(pq);
			}
		} catch (Exception e) {
			// Log.e(TAG,"READING INPUT",e);
			// Checked exceptions considered harmful.
		}

	}
	
	public FourByFourBoard generateFourByFourBoard() {
		return new FourByFourBoard(generateBoard(16));
	}

	public String[] generateBoard(int size) {
		int total = 0;
		Random rng = new Random();

		String board[] = new String[size];

		for(int i=0;i<charProbs.size();i++) {
			total += charProbs.get(i).peekProb();
		}

		// get the letters
		for(int i=0;i<size;i++) {
			ProbabilityQueue pq = null;
			int remaining = rng.nextInt(total);
			// Log.d(TAG,"remaining:"+remaining+"/"+total);
			for(int j=0;j<charProbs.size();j++) {
				pq = charProbs.get(j);
				remaining -= pq.peekProb();
				if(pq.peekProb() > 0 && remaining <= 0) {
					break;
				}
			}
			board[i] = pq.getLetter();
			total -= pq.getProb();
			total += pq.peekProb();
		}

		// shuffle the letters
		for(int to=15;to>0;to--) {
			int from = rng.nextInt(to);
			String tmp = board[to];
			board[to] = board[from];
			board[from] = tmp;
		}

		return board;
	}

	private class ProbabilityQueue {
		private String letter;
		private LinkedList<Integer> probQueue;

		public ProbabilityQueue(String l) {
			letter = l;
			probQueue = new LinkedList<Integer>();
		}

		public String getLetter() {
			return letter;
		}

		public void addProb(String s) {
			probQueue.add(new Integer(s));
		}

		public int peekProb() {
			if(probQueue.isEmpty()) return 0;
			return probQueue.peek().intValue();
		}

		public int getProb() {
			if(probQueue.isEmpty()) return 0;
			return probQueue.remove().intValue();
		}

	}

}
