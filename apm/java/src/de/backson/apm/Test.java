package de.backson.apm;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	private static Scanner sc;
	
	public static void main(String[] args) {
		sc = new Scanner(System.in);
		String line;
		try {
			while (!(line = sc.nextLine()).equals("exit")) {
				runLine(line);
			}
		} finally {
			sc.close();
		}
	}
	
	private static void runLine(String line) {
		Pattern pattern = Pattern.compile("([+\\-]?[^\\s]+)\\s*([+\\-*/])\\s*([+\\-]?[^\\s]+)");
		Matcher matcher = pattern.matcher(line);
		if (matcher.matches()) {
			String a = matcher.group(1);
			String b = matcher.group(3);
			String op = matcher.group(2);
			DecimalInt aa = new DecimalInt(a);
			DecimalInt bb = new DecimalInt(b);
			switch (op.charAt(0)) {
			case '+':
				System.out.println(DecimalInt.add(aa, bb));
				break;
			case '-':
				System.out.println(DecimalInt.subtract(aa, bb));
				break;
			case '*':
				System.out.println(DecimalInt.multiply(aa, bb));
				break;
			case '/':
				DecimalInt[] res = DecimalInt.divide(aa, bb);
				if (res[1].getSign() != 0)
					System.out.println("" + res[0] + " r " + res[1]);
				else
					System.out.println("" + res[0]);
				break;
			default:
				System.out.println("Syntax error");
			}
		}
		else {
			DecimalInt a = new DecimalInt(line);
			System.out.println(a);
		}
	}
	
}
