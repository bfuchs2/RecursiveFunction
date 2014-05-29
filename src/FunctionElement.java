import java.util.InputMismatchException;

/**
 * I had the idea for a way to store functions recursively during difEQ
 * and started writing it during Thermo
 * functions can be parsed from strings and dynamically edited
 * they can be evaluated for given values of x
 * and printed on the screen
 * @author Bernard */
public class FunctionElement {
	
	public short operator;
	public FunctionElement e1;
	public FunctionElement e2;
	public double value;
	
	public final static short ADD = 0;
	public final static short SUB = 1;
	public final static short TIMES = 2;
	public final static short DIV = 3;
	public final static short POW = 4;
	public final static short LOG = 5;
	public final static short SIN = 6;
	public final static short COS = 7;
	public final static short VAR = 8;
	public final static short CONST = 9;
	
	public final static String DIGS = "e0123456789.";
	
	/**
	 * easy way to create a new FunctionElement	 */
	public FunctionElement(FunctionElement e1, FunctionElement e2, short operator){
		this.e1 = e1;
		this.e2 = e2;
		this.operator = operator;
	}
	
	/**
	 * parses strings into FunctionElements recursively
	 * @throws InputMismatchException if there's a parenthetical mismatch or syntax error*/
	public FunctionElement(String s) throws InputMismatchException{
		s = s.replaceAll(" ", "").toLowerCase();//removes whitespace, goes to lower case
		try{
			if(s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')')
				s = s.substring(1, s.length() - 1);
		//if the entire string is enclosed in parenthesis they are removed
		}catch(StringIndexOutOfBoundsException e){
			throw new InputMismatchException("Syntax Error");
		}
		for(int i = 1; i < s.length(); i++){
			if(DIGS.indexOf(s.charAt(i - 1)) != -1 
					&& (s.charAt(i) == 'x' || s.charAt(i) == 'e')){
				s = s.substring(0, i) + "*" + s.substring(i, s.length());
			}//adds missing asterisks
		}
		if(count(s, '(') != count(s, ')')){
			throw new InputMismatchException("parenthetical mismatch");
		}//checks for correct parentheses
		boolean found = false;
		int i = 0;
		boolean backwards;
		short find;
		String info = null;
		for(find = 0; find <= CONST && !found; find++){
			backwards = find == SUB || find == DIV;
			i = backwards ? s.length() - 1 : 0;
			while(backwards ? i >= 0 : i < s.length()){//iterates through the string
				if(!backwards && count(s.substring(0, i), '(') >
				count(s.substring(0, i), ')')){
					i++;
					continue;//skip over parentheses
				}else if(backwards && count(s.substring(i, s.length()), '(') <
				count(s.substring(i, s.length()), ')')){
					i--;
					continue;
				}
				switch(find){
				case ADD:
					found = s.charAt(i) == '+';
					break;
				case SUB:
					found = s.charAt(i) == '-';
					break;
				case TIMES:
					found = s.charAt(i) == '*';
					break;
				case DIV:
					found = s.charAt(i) == '/';
					break;
				case POW:
					found = s.charAt(i) == '^';
					break;
				case LOG:
					try{
						if(s.substring(i, i+2).equals("ln")){
							found = true;
							info = "ln";
							value = Math.E;
						}else if(s.substring(i, i+4).equals("log_")){
							found = true;
							info = "log_";
						}else if(s.substring(i, i+3).equals("log")){
							found = true;
							info = "log";
							value = 10;
						}
					}catch(StringIndexOutOfBoundsException e){
						//string isn't long enough to have a log in it
					}
					break;
				case SIN:
					try{
						found = s.substring(i, i + 3).equals("sin");
					}catch(StringIndexOutOfBoundsException e){
						//string isn't long enough to have a sine in it
					}
					break;
				case COS:
					try{
						found = s.substring(i, i + 3).equals("cos");
					}catch(StringIndexOutOfBoundsException e){
						//string isn't long enough to have a cosine in it
					}
					break;
				case VAR:
					found = s.charAt(i) == 'x';
					break;
				case CONST:
					found = DIGS.indexOf(i) == -1;
					break;
				}
				if(!found){
					if(backwards){
						i--;
					}else{
						i++;
					}
				}else{
					break;
				}
			}
		}//once found is true it exists code
		if(!found){
			throw new InputMismatchException("couldn't find function elements in string");
		}
		//i is still in scope here--use to split function
		operator = --find;//find is always one greater than it should be for some reason
		if(operator == LOG || operator == SIN || operator == COS){
			if(info != null && info.equals("log_")){
				e1 = new FunctionElement(
						s.substring(s.indexOf('_', i) + 1, 
						s.indexOf('(', i)));//e1 gets the base of the log
			}else{
				e1 = null;
			}
			int begin = s.indexOf('(', i);
			int end = begin;
			while(count(s.substring(i, end + 1), '(' ) > count(s.substring(i, end + 1), ')' )){
				end++;//finds the matching closing paren to the one after i	
			}
			e2 = new FunctionElement(s.substring(begin, end + 1));
			//e2 gets what's in the parentheses
		}else if(operator == VAR){
			e1 = null;
			e2 = null;
		}else if(operator == CONST){
			int end = i;
			try{
				while(DIGS.indexOf(s.charAt(end)) != -1 && end < s.length()){
					end++;
				}
			}catch(StringIndexOutOfBoundsException e){
				end--;
			}
			if(s.substring(1, end + 1).equals("e")){
				value = Math.E;
			}else{
				value = Double.parseDouble(s.substring(i, end + 1));
			}
		}else{
			try{
				e1 = new FunctionElement(s.substring(0, i));
			}catch(StringIndexOutOfBoundsException e){
				e1 = null;
			}
			e2 = new FunctionElement(s.substring(i+1, s.length()));
		}
	}

	/**
	 * used to determine if there is a parenthetical mismatch */
	private int count(String s, char find) {
		int num = 0;
		for(char c : s.toCharArray()){
			if(c == find) num++;
		}
		return num;
	}
	
	/**
	 * evaluates the function for a given value of x
	 * @param val the value of x
	 * @return the result of the function
	 */
	public double evaluate(double val){
		switch(operator){
		case ADD:
			return e1.evaluate(val) + e2.evaluate(val);
		case SUB:
			return e1.evaluate(val) - e2.evaluate(val);
		case TIMES:
			return e1.evaluate(val) * e2.evaluate(val);
		case DIV:
			return e1.evaluate(val) / e2.evaluate(val);
		case POW:
			return Math.pow(e1.evaluate(val), e2.evaluate(val));
		case LOG:
			if(e1 == null){
				if(value == 10){
					return Math.log10(e2.evaluate(val));
				}else if(value == Math.E){
					return Math.log(e2.evaluate(val));
				}
			}else{
				return Math.log(e2.evaluate(val)) / Math.log(e1.evaluate(val));
			}
		case SIN:
			return Math.sin(e2.evaluate(val));
		case COS:
			return Math.cos(e2.evaluate(val));
		case VAR:
			return val;
		case CONST:
			return value;
		}
		return -1;
	}
	
	/**
	 a string representation of the function
	 * @see java.lang.Object#toString() */
	public String toString(){
		String op = null;
		switch(operator){
		case ADD:
			op = " + ";
			break;
		case SUB:
			op = " - ";
			break;
		case TIMES:
			if(e1.operator == CONST && e2.operator == VAR){
				op = "";
			}else{
				op = "*";
			}
			break;
		case DIV:
			return(e1.toString() + "/(" + e2.toString() + ")");
		case POW:
			return(e1.toString() + "^(" + e2.toString() + ")");
		case LOG:
			if(e1 == null){
				if(value == 10){
					return "log(" + e2.toString() + ")";
				}else if(value == Math.E){
					return "ln(" + e2.toString() + ")";
				}
			}else{
				return "log_" + e1.toString() + "(" + e2.toString() + ")";
			}
		case SIN:
			return "sin(" + e2.toString() + ")";
		case COS:
			return "cos(" + e2.toString() + ")";
		case VAR:
			return "x";
		case CONST:
			return String.valueOf(value);
		}
		return e1.toString() + op + e2.toString();
	}
	
	public double solve(){
		return solve(1, 1);
	}
	public double solve(double guess){
		return solve(guess, 1);
	}
	public double solve(double a, double offset) throws StackOverflowError{
		double comp = Math.abs(evaluate(a));
		if(comp < Double.MIN_NORMAL*1024){
			return a;
		}else if(comp > Math.abs(evaluate(-a))){
			return solve(-a, offset);
		}else if(Math.abs(evaluate(a - offset)) < comp){
			return solve(a - offset, offset);
		}else if(Math.abs(evaluate(a + offset)) < comp){
			return solve(a + offset, offset);
		}else
			return solve(a, offset/2);
	}
}
