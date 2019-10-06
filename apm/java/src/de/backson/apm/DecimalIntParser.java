package de.backson.apm;

import java.util.ArrayList;
import java.util.List;

public class DecimalIntParser {
	private String string;
	
	private List<Byte> digits;
	private int sign;
	private int base;
	
	public DecimalIntParser(String s) {
		parse(s);
	}
	
	public List<Byte> getDigits() {
		return digits;
	}
	
	public int getSign() {
		return sign;
	}
	
	public int getBase() {
		return base;
	}
	
	public void parse(String s) {
		string = s;
		parse();
	}
	
	private enum ParsingState {
		START, SIGN, ZERO, BASE, ZEROS, DIGITS
	}
	
	private int index;
	private ParsingState state;
	private String message;
	
	public void parse() {
		
		index = 0;
		state = ParsingState.START;
		
		digits = new ArrayList<Byte>();
		sign = 1;
		base = 10;
		
		while (index < string.length()) {
			switch (state) {
			case START:
				stateStart();
				break;
			case SIGN:
				stateSign();
				break;
			case ZERO:
				stateZero();
				break;
			case BASE:
				stateBase();
				break;
			case ZEROS:
				stateZeros();
				break;
			case DIGITS:
				stateDigits();
				break;
			default:
				throw new RuntimeException("Illegal internal state '" + state + "'");
			}
		}
		
		switch (state) {
		case ZERO:
		case ZEROS:
		case DIGITS:
			break;
		default:
			throw new RuntimeException("Syntax error: Unexpected end of string");
		}
	}

	private void stateStart() {
		char c = string.charAt(index);
		switch (c) {
		// plus sign
		case '+':
			sign = +1;
			state = ParsingState.SIGN;
			index++;
			break;
		// minus sign
		case '-':
			sign = -1;
			state = ParsingState.SIGN;
			index++;
			break;
		// zero
		case '0':
			state = ParsingState.ZERO;
			index++;
			break;
		// other digits
		default:
			state = ParsingState.DIGITS;
			break;
		}
	}
	
	private void stateSign() {
		char c = string.charAt(index);
		switch (c) {
		// zero
		case '0':
			state = ParsingState.ZERO;
			index++;
			break;
		// other digits
		default:
			state = ParsingState.DIGITS;
			break;
		}
	}

	private void stateZero() {
		char c = string.charAt(index);
		switch (c) {
		// binary
		case 'b':
		case 'B':
			base = 2;
			state = ParsingState.BASE;
			index++;
			break;
		// hex
		case 'x':
		case 'X':
			base = 16;
			state = ParsingState.BASE;
			index++;
			break;
		// other digits
		case '0':
			state = ParsingState.ZEROS;
			break;
		default:
			state = ParsingState.DIGITS;
			break;
		}
	}

	private void stateBase() {
		state = ParsingState.ZEROS;
	}

	private void stateZeros() {
		char c = string.charAt(index);
		switch (c) {
		case '0':
			index++;
			break;
		// other digits
		default:
			state = ParsingState.DIGITS;
			break;
		}
	}

	private void stateDigits() {
		char c = string.charAt(index);
		byte b = -1;
		switch (c) {
		case '0': b = 0; break;
		case '1': b = 1; break;
		case '2': b = 2; break;
		case '3': b = 3; break;
		case '4': b = 4; break;
		case '5': b = 5; break;
		case '6': b = 6; break;
		case '7': b = 7; break;
		case '8': b = 8; break;
		case '9': b = 9; break;
		case 'a':
		case 'A': b = 10; break;
		case 'b':
		case 'B': b = 11; break;
		case 'c':
		case 'C': b = 12; break;
		case 'd':
		case 'D': b = 13; break;
		case 'e':
		case 'E': b = 14; break;
		case 'f':
		case 'F': b = 15; break;
		default: break;
		}
		
		if (b < 0 || b >= base) {
			throw new IllegalArgumentException("Illegal symbol at position " + index + " of string '" + string + "'");
		}
		
		index++;
		
		digits.add(b);
	}
}
