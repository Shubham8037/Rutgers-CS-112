package math;

/**
 * This class encapsulates a BigInteger, i.e. a positive or negative integer
 * with any number of digits, which overcomes the computer storage length
 * limitation of an integer.
 * 
 */
public class BigInteger {

	/**
	 * True if this is a negative integer
	 */
	boolean negative;

	/**
	 * Number of digits in this integer
	 */
	int numDigits;

	/**
	 * Reference to the first node of this integer's linked list representation
	 * NOTE: The linked list stores the Least Significant Digit in the FIRST node.
	 * For instance, the integer 235 would be stored as: 5 --> 3 --> 2
	 */
	DigitNode front;

	/**
	 * Initializes this integer to a positive number with zero digits, in other
	 * words this is the 0 (zero) valued integer.
	 */
	public BigInteger() {
		negative = false;
		numDigits = 0;
		front = null;
	}

	/**
	 * Parses an input integer string into a corresponding BigInteger instance. A
	 * correctly formatted integer would have an optional sign as the first
	 * character (no sign means positive), and at least one digit character
	 * (including zero). Examples of correct format, with corresponding values
	 * Format Value +0 0 -0 0 +123 123 1023 1023 0012 12 0 0 -123 -123 -001 -1 +000
	 * 0
	 * 
	 * 
	 * @param integer
	 *            Integer string that is to be parsed
	 * @return BigInteger instance that stores the input integer
	 * @throws IllegalArgumentException
	 *             If input is incorrectly formatted
	 */
	public static BigInteger parse(String integer) throws IllegalArgumentException {
		// make object
		BigInteger bigInt = new BigInteger();
		// clear leading and tailing spaces
		integer = integer.trim();
		if (integer.length() == 0)
			throw new IllegalArgumentException();
		// adapt sign
		if (integer.charAt(0) == '+' || integer.charAt(0) == '-') {
			if (integer.charAt(0) == '-')
				bigInt.negative = true;
			integer = integer.substring(1);
		}
		// clear 0's in starting
		if (integer.charAt(0) == '0') {
			int zeroCount = 1;
			for (int i = 1; i < integer.length(); i++) {
				if (integer.charAt(i) != '0')
					break;
				zeroCount++;
			}
			if (zeroCount == integer.length()) {
				bigInt.front = null;
				return bigInt;
			} else
				integer = integer.substring(zeroCount, integer.length());
		}
		for (int i = 0; i < integer.length(); i++) {
			if (Character.isDigit(integer.charAt(i)))
				bigInt.front = new DigitNode((int) (integer.charAt(i) - '0'), bigInt.front);
			else
				throw new IllegalArgumentException();
		}
		bigInt.numDigits = integer.length();
		return bigInt;
	}

	/**
	 * Adds an integer to this integer, and returns the result in a NEW BigInteger
	 * object. DOES NOT MODIFY this integer. NOTE that either or both of the
	 * integers involved could be negative. (Which means this method can effectively
	 * subtract as well.)
	 * 
	 * @param other
	 *            Other integer to be added to this integer
	 * @return Result integer
	 */
	public BigInteger add(BigInteger other) {
		BigInteger bigInt = new BigInteger();
		boolean toSub = other.negative ^ this.negative;
		if (toSub) {
			// subtract
			// determine larger term
			boolean thisLarger; // is this object value larger than provided object
			if (this.numDigits > other.numDigits)
				thisLarger = true;
			else if (this.numDigits < other.numDigits)
				thisLarger = false;
			else {
				// digits are same, try comparing them in string form
				String This, Other;
				This = this.toString();
				Other = other.toString();
				if (this.negative)
					This = This.substring(1);
				if (other.negative)
					Other = Other.substring(1);
				if (This.equals(Other)) // cancel each other result is 0
					return bigInt;
				else
					thisLarger = This.compareTo(Other) > 0;
			}
			// process
			int sum, adigit;
			boolean carry = false;
			DigitNode it_a, it_b, new_node, new_it;
			if (thisLarger) {
				bigInt.negative = this.negative;
				it_a = this.front;
				it_b = other.front;
			} else {
				bigInt.negative = !this.negative;
				it_a = other.front;
				it_b = this.front;
			}
			//subtract
			while (it_a != null) {
				if (carry) {
					if (it_a.digit == 0)
						adigit = 9;
					else {
						adigit = it_a.digit - 1;
						carry = false;
					}
				} else
					adigit = it_a.digit;
				
				if(it_b != null) {
					if (adigit == it_b.digit)
						sum = 0;
					else if (adigit > it_b.digit)
						sum = adigit - it_b.digit;
					else {
						sum = adigit + 10 - it_b.digit;
						carry = true;
					}
				}else
					sum = adigit;
				
				new_node = new DigitNode(sum, null);
				it_a = it_a.next;
				if (it_b != null)
					it_b = it_b.next;
				// append to last
				if (bigInt.front == null)
					bigInt.front = new_node;
				else {
					new_it = bigInt.front;
					while (new_it.next != null)
						new_it = new_it.next;
					new_it.next = new_node;
				}
				bigInt.numDigits++;
			}
			//check for additional 0's
			String test = bigInt.toString();
			if(bigInt.negative)
				test = test.substring(1);
			if(test.startsWith("0"))
				bigInt = BigInteger.parse(bigInt.toString());
		} else {
			// add
			int sum;
			boolean carry = false;
			DigitNode it_a, it_b, new_node, new_it;
			it_a = other.front;
			it_b = this.front;
			while (it_a != null || it_b != null || carry) {
				sum = (it_a == null ? 0 : it_a.digit) + (it_b == null ? 0 : it_b.digit) + (carry ? 1 : 0);
				carry = false;
				if (sum > 9) {
					carry = true;
					sum %= 10;
				}
				new_node = new DigitNode(sum, null);
				if (it_a != null)
					it_a = it_a.next;
				if (it_b != null)
					it_b = it_b.next;
				// append to last
				if (bigInt.front == null)
					bigInt.front = new_node;
				else {
					new_it = bigInt.front;
					while (new_it.next != null)
						new_it = new_it.next;
					new_it.next = new_node;
				}
				bigInt.numDigits++;
			}
			bigInt.negative = this.negative;
		}
		return bigInt;
	}

	/**
	 * Returns the BigInteger obtained by multiplying the given BigInteger with this
	 * BigInteger - DOES NOT MODIFY this BigInteger
	 * 
	 * @param other
	 *            BigInteger to be multiplied
	 * @return A new BigInteger which is the product of this BigInteger and other.
	 */
	public BigInteger multiply(BigInteger other) {
		BigInteger bigInt = new BigInteger();
		int carry,mul;
		DigitNode it_a,it_b;
		it_b = this.front;
		String result;
		int iteration = 0;
		while(it_b != null) {
			result = "";
			//multiply with one digit
			it_a = other.front;
			carry = 0;
			while(it_a != null) {
				mul = (it_a.digit * it_b.digit) + carry;
				carry = 0;
				if(mul > 9) {
					carry = (int)(mul / 10);
					mul %= 10;
				}
				result = mul + result;
				it_a = it_a.next;
			}
			if(carry != 0)
				result = carry + result;
			if(iteration > 0)
				for(int i = 0; i < iteration; i++)
					result = result + "0";
			bigInt = bigInt.add(BigInteger.parse(result));
			iteration++;
			it_b = it_b.next;
		}
		bigInt.negative = other.negative ^ this.negative;
		return bigInt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (front == null) {
			return "0";
		}

		String retval = front.digit + "";
		for (DigitNode curr = front.next; curr != null; curr = curr.next) {
			retval = curr.digit + retval;
		}

		if (negative) {
			retval = '-' + retval;
		}

		return retval;
	}

}
