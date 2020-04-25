package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		/** COMPLETE THIS METHOD **/
		
		TrieNode root = new TrieNode(null, null, null);
		addAllStrings(root,allWords);
		return root;
	}
	
	private static void addAllStrings(TrieNode root, String[] values) {
		String first = values[0];
		TrieNode toBeAdded = new TrieNode(new Indexes(0,(short)0,(short)(first.length() - 1)), null, null);
		root.firstChild = toBeAdded;
		
		for (int i = 1; i < values.length;i++) {
			addOneString(root.firstChild, values, i);
		}
	}
	
	private static void addOneString(TrieNode parent, String[] values, int index) {
		addString(parent,values[index],values,index);
	}
	
	private static void addString(TrieNode parent,String value, String[] array,int index) {
		boolean toBeAdded = false;
		boolean toBeBroken = false;
		int position = -1;
		String newString = array[index];
		while (!toBeAdded) {
			String toBeMatched = array[parent.substr.wordIndex].substring(parent.substr.startIndex, parent.substr.endIndex + 1);
			if (fullMatch(toBeMatched,newString)) {
				parent = parent.firstChild;
				newString = newString.substring(parent.substr.startIndex);
				continue;
			}else if (noMatch(toBeMatched,newString)) {
				if (parent.sibling == null) {
					toBeAdded = true;
					break;
				}else {
					parent = parent.sibling;
					continue;
				}
			}else {
				position = getBreakPosition(toBeMatched, newString);
				if (position != -1){
					toBeAdded = toBeBroken = true;
					break;
				}
			}
		}
		if (toBeAdded && !toBeBroken) {
			TrieNode node = new TrieNode(new Indexes(index,parent.substr.startIndex,(short)(array[index].length() - 1)), null, null);
			parent.sibling = node;
		}
		
		if (toBeAdded && toBeBroken) {
			int startIndex = parent.substr.startIndex + position + 1;
			TrieNode broken = new TrieNode(new Indexes(parent.substr.wordIndex, (short)(startIndex), parent.substr.endIndex), parent.firstChild, null);
			parent.substr.endIndex = (short)(startIndex - 1);
			parent.firstChild = broken;
			TrieNode node = new TrieNode(new Indexes(index,(short)(startIndex),(short)(array[index].length() - 1)), null, null);
			broken.sibling = node;
		}
	}
	
	private static int getBreakPosition(String one, String two) {
		int toBeReturned = -1;
		int minimum = Math.min(one.length(), two.length());
		for (int i = 0; i < minimum;i++) {
			if (one.charAt(i) != two.charAt(i)) {
				toBeReturned = i - 1;
				break;
			}
		}
		return toBeReturned;
	}
	
	private static boolean fullMatch(String one, String two) {
		if (two.startsWith(one)) {
			return true;
		}else {
			return false;
		}
	}
	
	private static boolean noMatch(String one, String two) {
		if (one.charAt(0) != two.charAt(0)) {
			return true;
		}else {
			return false;
		}
	}
	
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		ArrayList<TrieNode> leafs = new ArrayList<>();
		TrieNode parent = root.firstChild;
		boolean toBeRepeated = true;
		boolean getAllLeafs = false;
		while (toBeRepeated) {
			String fromTrie = allWords[parent.substr.wordIndex].substring(parent.substr.startIndex, parent.substr.endIndex + 1);
			if (noMatch(fromTrie, prefix)) {
				parent = parent.sibling;
				continue;
			}
			if (fromTrie.startsWith(prefix)) {
				toBeRepeated = false;
				getAllLeafs = true;
				break;
			}
			if (prefix.startsWith(fromTrie)) {
				parent = parent.firstChild;
				prefix = prefix.substring(parent.substr.startIndex);
				continue;
			}
		}
		
		if (getAllLeafs) {
			leafs.addAll(getAllLeafs(parent));
		}
		return leafs;
	}
	
	private static ArrayList<TrieNode> getAllLeafs(TrieNode parent){
		ArrayList<TrieNode> toBeReturned = new ArrayList<>();
		if (parent.firstChild == null) {
			toBeReturned.add(parent); 
		}else {
			TrieNode child = parent.firstChild;
			do {
				toBeReturned.addAll(getAllLeafs(child));
				child = child.sibling;
			}while(child != null);
		}
		return toBeReturned;
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
