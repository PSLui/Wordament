package com.arist.trie;

import java.io.FileReader;
import java.io.BufferedReader;

import java.io.FileOutputStream;
import java.io.DataOutputStream;

public class TrieDictBuilder {

	private static void readFileIntoTrie(String dictFile, Trie trie,
		boolean usWord, boolean ukWord) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(dictFile));
			String line;
			while((line = br.readLine()) != null) {
				//System.out.println(line);
				trie.addWord(line,usWord,ukWord);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String [] Args) {

		if(Args.length < 3) {
			System.out.println("Usage: java -jar dicttool usdict ukdict outfile");
			System.exit(1);
		}

		Trie outTrie = new Trie();

		readFileIntoTrie(Args[0],outTrie,true,false);
		readFileIntoTrie(Args[1],outTrie,false,true);
		
		try {
			FileOutputStream of = new FileOutputStream(Args[2],false);
			outTrie.write(new DataOutputStream(of));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

