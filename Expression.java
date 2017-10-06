package apps;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols() {
    		/** COMPLETE THIS METHOD **/
    	scalars = new ArrayList<>();
    	arrays = new ArrayList<>();
    	String token = new String();
    	for (int i = 0; i < expr.length();i++) {
    		String temp = String.valueOf(expr.charAt(i));
    		if (delims.contains(temp)) {
    			if (token.isEmpty()) {
    				continue;
    			}
    			if (temp.equals("[")) {
    				ArraySymbol as = new ArraySymbol(token);
    				if (!arrays.contains(as)) {
    					arrays.add(as);
    				}
    			}else {
    				if (Character.isDigit(token.charAt(0))) {
    					token = new String();
    					continue;
    				}
    				ScalarSymbol ss = new ScalarSymbol(token);
    				if (!scalars.contains(ss)){
    					scalars.add(ss);
    				}
    			}
    			token = new String();
    		}else {
    			token += temp;
    		}
    	}
    	if (!token.isEmpty()) {
    		if (!Character.isDigit(token.charAt(0))) {
    			ScalarSymbol ss = new ScalarSymbol(token);
				if (!scalars.contains(ss)){
					scalars.add(ss);
				}
    		}
    	}
    }
    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    public float evaluate() {
    		/** COMPLETE THIS METHOD **/
    		// following line just a placeholder for compilation
    	System.out.println("Scalar Components are...");
    	printScalars();
    	System.out.println("Array Components are...");
    	printArrays();
    	expr = expr.replace("\t", "");
    	expr = expr.replace(" ", "");
    	return evaluate(expr);
    		//return 0;
    }
    
    private float evaluate(String expression) {
    	ArrayList<String> indexedExpression = getIndexedExpression(expression);
    	if (indexedExpression.size() == 1) {
    		String value = indexedExpression.get(0);
    		if (value.charAt(0) == '#') {
    			value = value.replace("#", "-");
    			return Float.parseFloat(value);
    		}else if (Character.isDigit(value.charAt(0))){
    			return Float.parseFloat(value);
    		}else {
    			ScalarSymbol ss = new ScalarSymbol(value);
    			int index = scalars.indexOf(ss);
    			if (index == -1) {
    				return 0;
    			}else {
    				return (float)scalars.get(index).value;
    			}
    		}
    		
    	}
    	Stack<String> operandStack = new Stack();
    	Stack<String> operatorStack = new Stack();
    	
    	for (String token : indexedExpression) {
    		switch (token) {
    		case "+":
    		case "-":
    			if (operatorStack.isEmpty()) {
    				operatorStack.push(token);
    			}else if (!((operatorStack.peek().equals("(")) || (operatorStack.peek().equals("[")))){
    				String op2 = operandStack.pop();
					String op1 = operandStack.pop();
					String subExpr = op1 + operatorStack.pop() + op2;
					float value = evaluate(subExpr);
					String result = String.valueOf(value);
					if (value < 0) {
						result = result.replace("-","#");
					}
					operandStack.push(result);
					operatorStack.push(token);
    			}else {
    				operatorStack.push(token);
    			}
    			break;
    		case "*":
    		case "/":
    			if (operatorStack.isEmpty()) {
    				operatorStack.push(token);
    			} else if (operatorStack.peek().equals("*") || operatorStack.peek().equals("/")) {
    				String op2 = operandStack.pop();
					String op1 = operandStack.pop();
					String subExpr = op1 + operatorStack.pop() + op2;
					float value = evaluate(subExpr);
					String result = String.valueOf(value);
					if (value < 0) {
						result = result.replace("-","#");
					}
					operandStack.push(result);
					operatorStack.push(token);
    			}else {
    				operatorStack.push(token);
    			}
    			break;
    		case "(":
    		case "[":
    			operatorStack.push(token);
    			break;
    		case ")":
    			while (!operatorStack.peek().equals("(")){
    				String op2 = operandStack.pop();
					String op1 = operandStack.pop();
					String subExpr = op1 + operatorStack.pop() + op2;
					float value = evaluate(subExpr);
					String result = String.valueOf(value);
					if (value < 0) {
						result = result.replace("-","#");
					}
					operandStack.push(result);
    			}
    			operatorStack.pop();
    			break;
    		case "]":
    			while (!operatorStack.peek().equals("[")){
    				String op2 = operandStack.pop();
					String op1 = operandStack.pop();
					String subExpr = op1 + operatorStack.pop() + op2;
					float value = evaluate(subExpr);
					String result = String.valueOf(value);
					if (value < 0) {
						result = result.replace("-","#");
					}
					operandStack.push(result);
    			}
    			operatorStack.pop();
    			String arrayIndex = operandStack.pop();
    			String array = operandStack.pop();
    			ArraySymbol as = new ArraySymbol(array);
    			int ai = arrays.indexOf(as);
    			int index = (int)Float.parseFloat(arrayIndex);
    			if (ai == -1) {
    				operandStack.push("0");
    			}else {
    				operandStack.push(String.valueOf(arrays.get(ai).values[index]));
    			}
    			break;
    		default:
    			operandStack.push(token);
    			break;
    		}
    		
    	}
    	
    	while (!operatorStack.isEmpty()) {
    		float op2 = evaluate(operandStack.pop());
    		float op1 = evaluate(operandStack.pop());
    		String operator = operatorStack.pop();
    		float value = process(op1,operator,op2);
    		String result = String.valueOf(value);
			if (value < 0) {
				result = result.replace("-","#");
			}
			operandStack.push(result);
    	}
    	
    	return Float.valueOf(evaluate(operandStack.pop()));
    }
    
    private ArrayList<String> getIndexedExpression(String expression) {
    	ArrayList<String> indexedExpression = new ArrayList<>();
    	String token = new String();
    	for (int i = 0; i < expression.length(); i++) {
    		String temp = String.valueOf(expression.charAt(i));
    		if (delims.contains(temp)) {
    			if (!token.isEmpty()) {
    				indexedExpression.add(token);
    				token = new String();
    			}
    			indexedExpression.add(temp);
    		}else {
    			token += temp;
    		}
    	}
    	if (!token.isEmpty()) {
    		indexedExpression.add(token);
    	}
    	return indexedExpression;
    }
    
    private float process(float op1, String operator, float op2) {
    	float result;
    	switch (operator) {
    	case "+":
    		result = op1 + op2;
    		break;
    	case "-":
    		result = op1 - op2;
    		break;
    	case "*":
    		result = op1 * op2;
    		break;
    	case "/":
    		result = op1 / op2;
    		break;
    	default:
    		result = 0.0f;
    		break;
    	}
    	return result;
    }

    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }

}
