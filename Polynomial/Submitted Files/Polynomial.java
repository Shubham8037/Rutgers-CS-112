package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
			//First node in our list (smallest degree)
			Node first = null;
			//Current node
			Node current = null;
			//Last node
			Node last = null;
			//To determine first node 
			int run = 0;
			
		
		while(poly1!=null||poly2!=null) {	
		//In case first polynomial is already done
		if(poly1==null) {
			current = new Node(poly2.term.coeff,poly2.term.degree,null);
			poly2 = poly2.next;
		}
		//In case second polynomial is already done
		else if(poly2==null) {
			current = new Node(poly1.term.coeff,poly1.term.degree,null);
			poly1 = poly1.next;
		}
		//New node is smaller one of both degrees
		else if(poly1.term.degree < poly2.term.degree) {
			current = new Node(poly1.term.coeff, poly1.term.degree, null);
			poly1 = poly1.next;
		}
		//New node is smaller one of both degrees
		else if (poly1.term.degree > poly2.term.degree) {
			current = new Node(poly2.term.coeff,poly2.term.degree, null);
			poly2 = poly2.next;
		}
		//If both values are of same degree, coeffs are added
		else {
			if(poly1.term.coeff+poly2.term.coeff != 0)
			current = new Node(poly1.term.coeff+poly2.term.coeff,poly1.term.degree,null);
			poly1 = poly1.next;
			poly2 = poly2.next;
		}
			//Save pointer to first node in list 
			if(run == 0)
				first = current;
			run++;
			
			//To get ascending order, current Node will be pointed at by the last Node in the list.
			if(last != null)
				last.next = current;
				last = current;
			
		}
			return first;
	}
	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		Node multiplicatedPoly = null;
		Node current1 = poly1;
		
		
		while(current1!=null) {
			Node current2 = poly2;
			//Multiplicate each term of the first poly with each term of the second poly
			while(current2!=null) {
				if(current1.term.coeff*current2.term.coeff != 0)
				multiplicatedPoly = new Node(current1.term.coeff*current2.term.coeff,current1.term.degree+current2.term.degree,multiplicatedPoly);
				current2=current2.next;
			}
			current1 = current1.next;
		}
		
		Node polyProduct = null;
		Node polyProductFirst = null;
		

		//At the moment there may be more than 1 term of each degree in the multiplicatedPoly List.
		//We will iterate through the list and sum up all coefficients of a single degree.
		for(int i = 0; i < 20; i++) {
			
			//To determine first term of degree i found
			int numFound = 0;
			Node current = multiplicatedPoly;
			
			//Iterate through all nodes
			while(current !=null) 
			{
				//And if degree equals i
				if(current.term.degree == i) 
				{
					//Either create a new node if none of this degree exists or add the value of the coefficient to the existing node
					if(numFound == 0) {
						//Store pointer to previous polyProduct
						Node polyProductOld = polyProduct;
						//Create new node polyProduct
						polyProduct = new Node(current.term.coeff,current.term.degree, null);
						//If list is empty (at first run of the loop)
						if(polyProductFirst == null)
							polyProductFirst = polyProduct;
						if(polyProductOld!=null)
						polyProductOld.next = polyProduct;
					}
					else
						//If there is already a node for this degree, just add the coefficient to it.
						polyProduct.term.coeff+=current.term.coeff;
					numFound++;
					
				}
				current = current.next;
			}
		}
		return polyProductFirst;
	}
		
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		float value = 0;
		while(poly!=null) {
			value+=poly.term.coeff*Math.pow(x, poly.term.degree);
			poly = poly.next;
		}
		return value;
			
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}
