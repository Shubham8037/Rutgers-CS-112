package solitaire;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

/**
 * This class implements a simplified version of Bruce Schneier's Solitaire Encryption algorithm.
 * 
 * @author RU NB CS112
 */
public class Solitaire {
	
	/**
	 * Circular linked list that is the deck of cards for encryption
	 */
	CardNode deckRear;
	
	public static void main (String[] args){
		
		Solitaire a = new Solitaire();
		
		a.makeDeck();
		a.printList(a.deckRear);
		
		a.jokerA();
		a.printList(a.deckRear);
		
		a.jokerB();
		a.printList(a.deckRear);
		
		a.tripleCut();
		a.printList(a.deckRear);
		
		a.countCut();
		a.printList(a.deckRear);
		
		a.getKey();
		a.printList(a.deckRear);
		
	}
	
	/**
	 * Makes a shuffled deck of cards for encryption. The deck is stored in a circular
	 * linked list, whose last node is pointed to by the field deckRear
	 */
	public void makeDeck() {
		// start with an array of 1..28 for easy shuffling
		int[] cardValues = new int[28];
		// assign values from 1 to 28
		for (int i=0; i < cardValues.length; i++) {
			cardValues[i] = i+1;
		}
		
		// shuffle the cards
		Random randgen = new Random();
 	        for (int i = 0; i < cardValues.length; i++) {
	            int other = randgen.nextInt(28);
	            int temp = cardValues[i];
	            cardValues[i] = cardValues[other];
	            cardValues[other] = temp;
	        }
	     
	    // create a circular linked list from this deck and make deckRear point to its last node
	    CardNode cn = new CardNode();
	    cn.cardValue = cardValues[0];
	    cn.next = cn;
	    deckRear = cn;
	    for (int i=1; i < cardValues.length; i++) {
	    	cn = new CardNode();
	    	cn.cardValue = cardValues[i];
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
	    }
	}
	
	/**
	 * Makes a circular linked list deck out of values read from scanner.
	 */
	public void makeDeck(Scanner scanner) 
	throws IOException {
		CardNode cn = null;
		if (scanner.hasNextInt()) {
			cn = new CardNode();
		    cn.cardValue = scanner.nextInt();
		    cn.next = cn;
		    deckRear = cn;
		}
		while (scanner.hasNextInt()) {
			cn = new CardNode();
	    	cn.cardValue = scanner.nextInt();
	    	cn.next = deckRear.next;
	    	deckRear.next = cn;
	    	deckRear = cn;
		}
	}
	
	/**
	 * Implements Step 1 - Joker A - on the deck.
	 */
	void jokerA() {
		
		if (deckRear.cardValue == 27)
		{
			deckRear.cardValue = (deckRear.next).cardValue;
			(deckRear.next).cardValue = 27;
		}
		else{
			for (CardNode temp=deckRear.next ; temp!=deckRear ; temp=temp.next)
			{
				if(temp.cardValue == 27)
				{
					if(temp.next.cardValue == deckRear.cardValue)
					{
						temp.cardValue = (temp.next).cardValue;
						(temp.next).cardValue = 27;
						break;
					}
					else
					{
						temp.cardValue = (temp.next).cardValue;
						(temp.next).cardValue = 27;
						break;
					}
				}	
			}
		}
		printList(deckRear);
	}
	
	/**
	 * Implements Step 2 - Joker B - on the deck.
	 */
	void jokerB() {
		
		if (deckRear.cardValue == 28)
		{
			deckRear.cardValue = (deckRear.next).cardValue;
			(deckRear.next).cardValue = (deckRear.next.next).cardValue;
			(deckRear.next.next).cardValue = 28;
		}
		
		else{
			for (CardNode temp=deckRear.next ; temp!=deckRear ; temp=temp.next)
			{
				if(temp.cardValue == 28)
				{
					temp.cardValue = (temp.next).cardValue;
					(temp.next).cardValue = (temp.next.next).cardValue;
					(temp.next.next).cardValue = 28;
					break;
				}
			}
		}
		printList(deckRear);
	}
	
	/**
	 * Implements Step 3 - Triple Cut - on the deck.
	 */
	void tripleCut() {
		CardNode J1 = new CardNode();
		CardNode J2 = new CardNode();
		CardNode J1prev = new CardNode();
		CardNode J2next = new CardNode();
		CardNode J1s = deckRear.next;
				
		// 27 at front
				if ((deckRear.next).cardValue == 27)
				{
					for (CardNode temp=deckRear.next ; temp!=deckRear ; temp=temp.next)
					{
						if (temp.cardValue == 28)
						{
							//DOES SOMETHING HERE
							J2 = temp;
							break;
						}
					}
					deckRear = J2;
				}
				
				// 27 at last
				else if (deckRear.cardValue == 27)
					{
						for (CardNode temp=deckRear.next ; temp!=deckRear ; temp=temp.next)
						{
							if (temp.next.cardValue == 28)
							{
								//DOES SOMETHING HERE
								J1prev = temp;
								J1 = temp.next;
								break;
							}
						}
						deckRear = J1prev;
					}	
				
				// 28 at front
				else if ((deckRear.next).cardValue == 28)
					{
						for (CardNode temp=deckRear.next ; temp!=deckRear ; temp=temp.next)
						{
							if (temp.next.cardValue == 27)
							{
								//DOES SOMETHING HERE
								J2 = temp.next;
								break;
							}
						}
						
						deckRear = J2;
					}
					
				// 28 at last
				else if (deckRear.cardValue == 28)
					{
						for (CardNode temp=deckRear.next ; temp!=deckRear ; temp=temp.next)
						{
							if (temp.next.cardValue == 27)
							{
								//DOES SOMETHING HERE
								J1prev = temp;
								J1 = temp.next;
								break;
							}
						}
						deckRear = J1prev;
					}
						
				
				else{
					
				//Assign J1
				for (CardNode temp=deckRear.next ; temp!=deckRear ; temp=temp.next)
				{
					if (temp.next.cardValue == 27 || temp.next.cardValue == 28)
					{
						J1prev = temp;
						J1 = temp.next;
						break;
					}
				}
				
				
				//Assign J2
				for (J2=J1.next ; J2!=deckRear ; J2=J2.next)
				{
					if (J2.cardValue == 27 || J2.cardValue == 28)
					{
						J2next = J2.next;
						break;
					}
				}
				
				//Shuffle
				J2.next = J1s;
				deckRear.next = J1;
				deckRear = J1prev;
				deckRear.next = J2next;
			}
				printList(deckRear);
	}
	
	/**
	 * Implements Step 4 - Count Cut - on the deck.
	 */
	void countCut() {

		// COMPLETE THIS METHOD
		
		int count = deckRear.cardValue;
		if(count==28)count--;
		
		CardNode node = deckRear;
		while(count>0){
			node = node.next;
			count--;
		}
		CardNode prev;
		for(prev = deckRear.next; prev.next!=deckRear; prev = prev.next){}
		CardNode first = deckRear.next;
		
		prev.next = first;
		deckRear.next = node.next;
		node.next = deckRear;
		printList(deckRear);

	}
	
	/**
	 * Gets a key. Calls the four steps - Joker A, Joker B, Triple Cut, Count Cut, then
	 * counts down based on the value of the first card and extracts the next card value 
	 * as key. But if that value is 27 or 28, repeats the whole process (Joker A through Count Cut)
	 * on the latest (current) deck, until a value less than or equal to 26 is found, which is then returned.
	 * 
	 * @return Key between 1 and 26
	 */
	int getKey() {
		
		// COMPLETE THIS METHOD
		
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE

		CardNode n = deckRear.next;
		int key= 0;
		int b = deckRear.next.cardValue;
		if (b==27 || b==28){
			b=27;
		}
		for (int i = 1; i!= b; i++){
			n = n.next;
		}
		 key = n.next.cardValue;
		 while( key==27 || key==28){
			 
				jokerA();
				jokerB();
				tripleCut();
				countCut();
				key = getKey();

			}
		 //System.out.println(key);
		    return key;
		    
	}
	
	/**
	 * Utility method that prints a circular linked list, given its rear pointer
	 * 
	 * @param rear Rear pointer
	 */
	private static void printList(CardNode rear) {
		if (rear == null) { 
			return;
		}
		System.out.print(rear.next.cardValue);
		CardNode ptr = rear.next;
		do {
			ptr = ptr.next;
			System.out.print("," + ptr.cardValue);
		} while (ptr != rear);
		System.out.println("\n");
	}

	/**
	 * Encrypts a message, ignores all characters except upper case letters
	 * 
	 * @param message Message to be encrypted
	 * @return Encrypted message, a sequence of upper case letters only
	 */
	public String encrypt(String message) {	
		// COMPLETE THIS METHOD
	    // THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		
		String n = "";
		for(int i=0; i<message.length(); ++i){
			Character a = message.charAt(i);
			int p = 0;
			if(Character.isLetter(a))
			{
				
				jokerA();
				jokerB();
				tripleCut();
				countCut();
				int key = getKey();
				System.out.println(key + "");
				a = Character.toUpperCase(a);
				p = (int)((char)a - 'A' + 1);
				p = (int)(p+key);
				while(p>26)
				{
					p = (int)(p - 26);
				}
				p = (int)(p +'A'-1);
				a = (char)p;
				n+=a;
				}
			else
				continue;
		}
		
		System.out.println(n);
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
	    return n;
	}
	
	/**
	 * Decrypts a message, which consists of upper case letters only
	 * 
	 * @param message Message to be decrypted
	 * @return Decrypted message, a sequence of upper case letters only
	 */
	public String decrypt(String message) {	
		// COMPLETE THIS METHOD
	    // THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		
		String n= "";
		for (int i=0; i<message.length(); ++i){
			int key=0;
			Character a = message.charAt(i);
			int p =0;
			if(Character.isLetter(a)){
				jokerA();
				jokerB();
				tripleCut();
				countCut();
				key = getKey();
			}
			else
				continue;
			
			p = (int)((char)a-'A'+1);
			p = (int)(p-key);
			while (p<1){
				p = (int)(p + 26);
			}
			p = (int)(p+'A'- 1);
			a = (char)p;
			n +=a;
		}
		System.out.println(n);
	    // THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
	    return n;
	}
}
