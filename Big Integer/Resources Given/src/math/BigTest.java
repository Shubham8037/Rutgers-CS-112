package math;

import java.io.IOException;
import java.util.Scanner;

public class BigTest {

	static Scanner sc;
	
	public static void parse() 
	throws IOException {
		System.out.print("\tEnter integer => ");
		String integer = sc.next();
		try {
			BigInteger bigInteger = BigInteger.parse(integer);
			System.out.println("\t\tValue = " + bigInteger);
		} catch (IllegalArgumentException e) {
			System.out.println("\t\tIncorrect Format");
		}
	}
	
	public static void add() 
	throws IOException {
		System.out.print("\tEnter first integer => ");
		String integer = sc.next();
		BigInteger firstBigInteger = BigInteger.parse(integer);
		
		System.out.print("\tEnter second integer => ");
		integer = sc.next();
		BigInteger secondBigInteger = BigInteger.parse(integer);
		
		System.out.println("\t\tSum: " + firstBigInteger.add(secondBigInteger));
	}
	
	public static void multiply() 
	throws IOException {
		System.out.print("\tEnter first integer => ");
		String integer = sc.next();
		BigInteger firstBigInteger = BigInteger.parse(integer);
		
		System.out.print("\tEnter second integer => ");
		integer = sc.next();
		BigInteger secondBigInteger = BigInteger.parse(integer);
		
		System.out.println("\t\tProduct: " + firstBigInteger.multiply(secondBigInteger));
		
	}
	
	public static void main(String[] args) 
	throws IOException {
		
		// TODO Auto-generated method stub
		sc = new Scanner(System.in);
		
		char choice;
		while ((choice = getChoice()) != 'q') {
			switch (choice) {
				case 'p' : parse(); break;
				case 'a' : add(); break;
				case 'm' : multiply(); break;
				default: System.out.println("Incorrect choice"); 
			}
		}
	}

	private static char getChoice() {
		System.out.print("\n(p)arse, (a)dd, (m)ultiply, or (q)uit? => ");
		String in = sc.next();
		char choice;
		if (in == null || in.length() == 0) {
			choice = ' ';
		} else {
			choice = in.toLowerCase().charAt(0);
		}
		return choice;
	}

}
