package com.arist.trie;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

public class CompressedTrie extends Trie {

	private static int[] FOLLOW_MASKS = {
		0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,
		0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,
		0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,
		0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,
		0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,0x3ffffff,
		0x3ffffff
	};

	public CompressedTrie(InputStream input) throws IOException {
		this(input,0x3ffffff,true,true);
	}

	public CompressedTrie(InputStream input, int mask, boolean usWords,
		boolean ukWords) throws IOException {
		this(input,mask,FOLLOW_MASKS,usWords,ukWords);
	}

	public CompressedTrie(InputStream input, int mask, int[] neighborMasks,
		boolean usWords, boolean ukWords) throws IOException {
		super();
		if(usWords) {
			mask |= US_WORD_BIT;
		}
		if(ukWords) {
			mask |= UK_WORD_BIT;
		}
		root = readTrie(new BufferedInputStream(input,8192),mask,
			neighborMasks,-1,true,true);
	}

	private Trie.TrieNode readTrie(InputStream input, int mask, 
		int[] neighborMasks, int value,boolean store, 
		boolean isRoot) throws IOException {
		int firstByte = input.read()<<24;

		if((firstByte&LEAF_BIT)!=0) {
			//This node is a tail.
			if(store) {
				if((firstByte&mask)!=0) {
					return EMPTY_LEAF.processWordBits(firstByte&mask);
				}
			} 
			return null;
		}

		int cBits = firstByte;
		cBits |= input.read()<<16;
		cBits |= input.read()<<8;
		cBits |= input.read();

		int nextBits = input.read()<<16;
		nextBits |= input.read()<<8;
		nextBits |= input.read();

		if(!store) {
			while(nextBits>0) {
				nextBits -= input.skip(nextBits);
			}
			return null;
		}

		int maskedBits = cBits&mask;
		if(value > -1) {
			maskedBits &= WORD_MASK|neighborMasks[value];
		}

		if(maskedBits != 0) {
			TrieNode ret = null;
			int childNumber = 0;
			for(int i=0;i<26;i++) {
				if((maskedBits&(1<<i))!=0) {
					TrieNode child = readTrie(input,mask,neighborMasks,i,
						true,false);
					if(child != null) {
						if(ret == null) {
							ret = new CompressedTrieNode(maskedBits);
						}
						ret.children[childNumber] = child;
						childNumber++;
					} else {
						maskedBits ^= (1<<i);
					}
				} else if((cBits&(1<<i))!=0) {
					readTrie(input,mask,null,-1,false,false);
				}
			}
			if(ret != null) {
				// node has valid children
				ret.childBits = maskedBits;
				return ret;
			} else if ((maskedBits&WORD_MASK) != 0) {
				// node has no valid children but is a word.
				return EMPTY_LEAF.processWordBits(cBits&WORD_MASK);
			}
			return null;
		}
		for(cBits&=LETTER_MASK;cBits!=0;cBits>>=1) {
			if((cBits&1)!=0) {
				readTrie(input,mask,null,-1,false,false);
			}
		}
		if((cBits&WORD_MASK)!=0) {
			return EMPTY_LEAF.processWordBits(cBits&WORD_MASK);
		}
		return null;
	}

	/**
	 * The CompressedTrieNode is like a normal TrieNode, but only allocates
	 * a large enough Array to store the number of children that it is known
	 * to have.
	 */
	protected class CompressedTrieNode extends Trie.TrieNode {
		public CompressedTrieNode(int cBits) {
			childBits = cBits;
			
			children = new TrieNode[countBits(cBits&LETTER_MASK)];
		}

		@Override
		protected TrieNode childAt(int index) {
			TrieNode ret = null;
			int j=0;
			for(int i=0;i<=index;i++) {
				if((childBits&(1<<i)) != 0) {
					ret = children[j];
					j++;
				} else {
					ret = null;
				}
			}
			return ret;
		}
	}

}

