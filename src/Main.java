import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**basically a wrapper class for the more fleshed-out and commented FunctionElement.java
 * since FunctionElements are largely recursive, a wrapper class was necessary
 * this class is mostly just to make sure the other one works
 * @author Bernard */
public class Main{

	public static void main(String[] args){
		Scanner scanner = new Scanner(System.in);
		ArrayList<FunctionElement> functions = new ArrayList<FunctionElement>();
		// String string = scanner.nextLine();
		String string = null;
		while(true){
			try{
				System.out.println("What would you like to do? (\"help\" for help)");
				string = scanner.nextLine().toLowerCase();
				if(string.startsWith("add")) {
					try{
						functions.add(new FunctionElement(string
								.replaceAll("add", "").trim()));
					}catch(Exception e){
						System.out.println("Enter the function now");
						string = scanner.nextLine();
						try{
							functions.add(new FunctionElement(string));
						}catch (InputMismatchException em) {
							System.out.println(em.getMessage());
						}
					}
					System.out.println(functions.get(functions.size() - 1)
							+ " has been added at index "
							+ (functions.size() - 1));
				}else if(string.startsWith("show")) {
					int place = selection(string, "show");
					System.out.println("function " + place + ": "
							+ functions.get(place));
				}else if(string.startsWith("eval")){
					int place = 0;
					double x = 0;
					try {
						System.out.println(
								"enter a functions and a value for x (comma separated)");
						string = scanner.nextLine();
						place = Integer.parseInt(string.substring(0,
								string.indexOf(',')).trim());
						x = Double.parseDouble(string.substring(
								string.indexOf(',') + 1, string.length())
								.trim());
					} catch (NumberFormatException e){
						e.printStackTrace();
					}
					System.out.println(functions.get(place) + " = "
									+ functions.get(place).evaluate(x)
									+ " at x = " + x);
				}else if(string.startsWith("solve")){
					int place = selection(string, "solve");
					System.out.println("guess?");
					double guess = scanner.nextDouble();
					try{
						System.out.println(functions.get(place) + 
								" = 0 at x = " +functions.get(place).solve(guess));
					}catch(StackOverflowError e){
						System.out.println("no solutions found");
					}
				} else {
					System.out.println("help has not been implemented");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static int selection(String s, String command){
		int place;
		try{
			place = Integer.parseInt(s.replaceAll(command, "")
					.replaceAll("show", "").trim());
		}catch(NumberFormatException e){
			System.out.println("select a function");
			Scanner scanner = new Scanner(System.in);
			place = scanner.nextInt();
		}
		return place;
	}
}
