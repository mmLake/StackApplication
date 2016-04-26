import java.util.Stack;
import java.util.Scanner;
//import java.util.ArrayList;

public class Project4 {
	
	Stack<Character> prefix = new Stack<Character>();
	Stack<Character> postfix = new Stack<Character>();
	Stack<String> operand = new Stack<String>();
	Scanner sc;
	String infix;
	String result;
	/*this integer only exists to keep the spacing even when printing out results.
	 * It represents the length of the postfix expression*/
	int postfixLength;
	
	Boolean quit = false;
	Boolean unbalancedPar = false;

	/* 
	 * Project4 is the constructor of this class.
	 * However its primary function acts as the main method, 
	 * since it directly or indirectly calls all of the methods within this class.
	*/
	public Project4() {
		startMessage();
		
		while (!quit){
			sc = new Scanner(System.in);
			
			readInfix();
			
			System.out.printf("%1s%30s%30s%n", "Infix:", "Postfix:", "Prefix:");
			System.out.print(infix);
			spacing(infix.length());
			postfix(infix);
			if (unbalancedPar){
				System.out.println("Infix has unbalanced parenthesis and is invalid. Program N/A");
			}
			else{
				spacing(postfixLength);
				prefix(infix);
				System.out.println();
			}
		}
		sc.close();
	}

	/*
	 * assume that each variable name is a single character and no spaces are
	 * left between characters when infix is entered. Will quit the program if user only presses enter
	 */
	public void readInfix() {
		
		System.out.printf("%nEnter arithmetic expression: ");
		infix = sc.nextLine();

		if(infix.isEmpty()) {
			quit = true;
			endMessage();
		}
	}

	/*The postfix method uses one stack and one local string variable to get the 
	 * postfix expression.
	 * This method also checks to see if the infix is unbalanced. If it is, it sets the Boolean class 
	 * variable unbalancedPar, to true. If it is not unbalanced, it prints out the postfix expression.  
	 * */
	public void postfix(String infix) {
		result = "";
		char[] curExp = infix.toCharArray();

		for (int i = 0; i < curExp.length; i++) {
			char c = curExp[i];

			if (isVariable(c)) {
				result += c;
			} else if (c == '(') {
				postfix.push(c);
			}

			else if (c == ')') {
				while ((!postfix.isEmpty()) && postfix.peek() != '(') {
					result += postfix.pop();
				}

				if (postfix.isEmpty()) {
					unbalancedPar = true;
					return;
				}
				postfix.pop();
			}

			else if (!postfix.isEmpty() && higherPrecedence(c, postfix.peek())) {
				postfix.push(c);
				//break;
			}

			else if (!postfix.isEmpty()) {
				while (!postfix.isEmpty()) {
					if (lowerPrecedence(c, postfix.peek()) || samePrecedence(postfix.peek(), c)) {
						result += postfix.pop();	
					} 
					else if(doubleParenthesis(c, postfix.peek())){
						postfix.pop();
					}
					else {break;}
				}
				postfix.push(c);
			}

			else {
				postfix.push(c);
			}
		}

		while (!postfix.isEmpty()) {
			if (postfix.peek() == '(') {
				unbalancedPar = true;
				return;
			}
			result += postfix.pop();
		}

		if (!unbalancedPar){
			postfixLength = result.length();
			System.out.print(result);
		}
	}

	/*The prefix method uses two stacks to keep track of the prefix expression. It has three local
	 * variables that keep track of the left hand operand, the right hand operand, and a newly combined
	 * operand, instead of one local string variable as with postfix. */
	public void prefix(String infix){
		String rightOp = "";
		String leftOp = "";
		String op = "";
		
		char[] curExp = infix.toCharArray();

		for (int i = 0; i < curExp.length; i++) {
			char c = curExp[i];

			if (isVariable(c)) {
				operand.push("" + c);
			} 
			else if (c == '(') {
				prefix.push(c);
			}
			else if (c == ')') {
				while ((!prefix.isEmpty()) && prefix.peek() != '(') {
					leftOp = operand.pop();
					rightOp = operand.pop();
					op = prefix.pop() + rightOp + leftOp;
					operand.push(op);
				}
				prefix.pop();
			}

			else if (!prefix.isEmpty() && higherPrecedence(c, prefix.peek())) {
				prefix.push(c);
				//break;
			}

			else if (!prefix.isEmpty()) {
				while (!prefix.isEmpty()) {
					if (lowerPrecedence(c, prefix.peek()) || samePrecedence(prefix.peek(), c)) {
						leftOp = operand.pop();
						rightOp = operand.pop();
						op = prefix.pop() + rightOp + leftOp;
						operand.push(op);
					} else {break;}
				}
				prefix.push(c);
			}

			else {
				prefix.push(c);
			}
		}	
		
		while(!prefix.isEmpty()){
			leftOp = operand.pop();
			rightOp = operand.pop();
			op = prefix.pop() + rightOp + leftOp;
			operand.push(op);
		}
		System.out.print(op);
	}
	
	/*The following five methods return Booleans. 
	 * They are used to determine what to do with the character that is currently being scanned
	 * from the infix expression*/
	public Boolean isVariable(char c) {
		return (c != '/' && c != '+' && c != '*' && c != '-' && c!= '%' && c != '(' && c != ')');
	}

	public Boolean higherPrecedence(char c, char d) {
		return ((c == '/' || c == '*' || c == '%') && (d == '+' || d == '-'));
	}

	public Boolean lowerPrecedence(char c, char d) {
		return ((d == '/' || d == '*' || d == '%') && (c == '+' || c == '-'));
	}

	public Boolean samePrecedence(char c, char d) {
		if ((c == '/' || c == '*' || c == '%') && (d == '/' || d == '*' || d == '%')) {
			return true;
		}
		if ((c == '+' || c == '-') && (d == '+' || d == '-')) {
			return true;
		}
		return false;
	}
	
	//prevents incorrect read that the infix is unbalanced, when it actually just has extra parenthesis
	public Boolean doubleParenthesis(char c, char d){
		return ((c == '(' && d == '(') || (c == ')' && d == ')' ));
	}
	
	//prints once, when the program is opened
	public static void startMessage(){
		System.out.printf("This program prints the prefix and postfix expression of" +
				"an infix expression.%nEach variable must be a single character. " +
				"Leave no spaces between characters.%n");
		
	}
	
	//a small method to help program print with correct spacing
	public static void spacing(int length){
		for (int i = 0; i < (30 - (length % 30)); i++){
			System.out.print(" ");
		}
	}
	// prints right before quitting the program
	public static void endMessage() {
		System.out.println("Program has ended");
		System.exit(0);
	}

	//main method. Only instantiates constructor.
	public static void main(String[] args) {
		Project4 p = new Project4();
	}
}
