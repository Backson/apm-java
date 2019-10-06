package de.backson.apm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DecimalInt {
	
	private byte[] mData;
	private int mSize;
	boolean mNegative;
	
	// initialize to zero
	public DecimalInt() {
		mData = null;
		mSize = 0;
		mNegative = false;
	}
	
	// initialize to value in decimal string (MSB first)
	public DecimalInt(String s) {
		DecimalIntParser parser = new DecimalIntParser(s);
		
		// set new size and allocate space
		int size = parser.getDigits().size();
		if (size > 0) {
			if (parser.getBase() == 10) {
				mSize = size;
				mData = new byte[size];
				// copy digits to data array in reverse order
				int i = mSize - 1;
				for (Byte b : parser.getDigits()) {
					mData[i--] = b;
				}
			}
			else {
				mData = new byte[size*2];
				for (Byte b : parser.getDigits()) {
					int carry = b;
					for (int i = 0; i < mData.length; ++i) {
						int sum = mData[i] * parser.getBase() + carry;
						int word = sum % 10;
						carry = sum / 10;
						mData[i] = (byte)word;
						if (word != 0) {
							mSize = Math.max(mSize, i + 1);
						}
					}
				}
			}
			mNegative = parser.getSign() < 0;
		}
		else {
			// zero requires no data array at all
			mData = null;
			// zero is not negative, i.e. "-0" equals "+0"
			mNegative = false;
		}
	}
	
	// initialize to value of an integer
	public DecimalInt(long val) {
		// handle sign
		if (val < 0) {
			mNegative = true;
		}
		else {
			mNegative = false;
		}
		// 19 digits fit every long there is
		// this is a bit wasteful, but avoids reallocation
		mData = new byte[19];
		// iterate over data
		int counter = 0;
		while (val != 0) {
			byte remainder = (byte)Math.abs(val % 10);
			val = val / 10;
			mData[counter++] = remainder;
		}
		mSize = counter;
		
		shrink();
	}
	
	// allocate enough memory for at least 'size' digits
	private void grow(int size) {
		if (size < 0)
			throw new IllegalArgumentException("Negative size");
		
		if (mData == null) {
			mData = new byte[size];
		}
		else if (size > mData.length) {
			mData = Arrays.copyOf(mData, size);
		}
	}
	
	// shrink the array to the smallest size possible
	private void shrink() {
		if (mData != null && mData.length > mSize) {
			mData = Arrays.copyOf(mData, mSize);
		}
	}
	
	// return size, i.e. the largest index of any nonzero 
	public int getSize() {
		return mSize;
	}
	
	// return true if negative, false otherwise
	public boolean isNegative() {
		return mNegative;
	}
	
	// return -1 if negative, +1 if positive and 0 otherwise
	public int getSign() {
		if (mSize == 0)
			return 0;
		else if (mNegative)
			return -1;
		else
			return +1;
	}
	
	// make a copy
	public DecimalInt copy() {
		DecimalInt result = new DecimalInt();
		result.mSize = mSize;
		result.mNegative = mNegative;
		result.mData = Arrays.copyOf(mData, mSize);
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if (mSize == 0) {
			sb.append("0");
		}
		else {
			if (mNegative) {
				sb.append("-");
			}
			for (int i = mSize - 1; i >= 0; --i) {
				sb.append("" + mData[i]);
			}
		}
		
		return sb.toString();
	}
	
    @Override
    public boolean equals(Object o) { 
  
        // If the object is compared with itself then return true   
        if (o == this) { 
            return true; 
        } 
  
        // check if o is the same type as this
        if (!(o instanceof DecimalInt)) { 
            return false; 
        } 
          
        // typecast o to Complex so that we can compare data members  
        DecimalInt c = (DecimalInt) o; 
          
        // Compare the data members and return accordingly  
        return DecimalInt.eq(c, this);
    } 
	
	// creates a DecimalInt equal to 10^exponent
	public static DecimalInt getPowerOfTen(int exponent) {
		if (exponent < 0)
			throw new IllegalArgumentException("Negative exponent " + exponent + " is not allowed.");
		
		DecimalInt result = new DecimalInt();
		result.grow(exponent + 1);
		result.mData[exponent] = (byte)1;
		result.mSize = exponent + 1;
		return result;
	}
	
	public static boolean lte(DecimalInt lhs, DecimalInt rhs) {
		return compare(lhs, rhs) <= 0;
	}
	
	public static boolean lt(DecimalInt lhs, DecimalInt rhs) {
		return compare(lhs, rhs) < 0;
	}
	
	public static boolean gte(DecimalInt lhs, DecimalInt rhs) {
		return compare(lhs, rhs) >= 0;
	}
	
	public static boolean gt(DecimalInt lhs, DecimalInt rhs) {
		return compare(lhs, rhs) > 0;
	}
	
	public static boolean eq(DecimalInt lhs, DecimalInt rhs) {
		return compare(lhs, rhs) == 0;
	}
	
	public static boolean neq(DecimalInt lhs, DecimalInt rhs) {
		return compare(lhs, rhs) != 0;
	}
	
	public static int compare(DecimalInt lhs, DecimalInt rhs) {
		// get the signs
		// zero counts as positive
		int lhsSign = lhs.getSign();
		int rhsSign = rhs.getSign();

		// check the signs
		if (lhsSign < rhsSign) {
			return -1;
		}
		if (lhsSign > rhsSign) {
			return 1;
		}

		// at this point we know that both values have the same sign.
		//assert(lhs_sign == rhs_sign);
		int sign = lhsSign;

		return sign * compareMagnitudes(lhs, rhs);
	}
	
	public static int compareMagnitudes(DecimalInt lhs, DecimalInt rhs) {
		int lhsSize = lhs.getSize();
		int rhsSize = rhs.getSize();
		// check if either number has more words
		if (lhsSize < rhsSize)
			return -1;
		if (lhsSize > rhsSize)
			return 1;

		// at this point we know that lhs->size == rhs->size
		//assert(lhs_size == rhs_size);
		int size = lhsSize;

		// iterate through the words, from MSD to LSD
		for (int i = size - 1; i >= 0; --i) {
			byte lhsWord = lhs.mData[i];
			byte rhsWord = rhs.mData[i];
			if (lhsWord < rhsWord)
				return -1;
			if (lhsWord > rhsWord)
				return 1;
		}

		// if we reach this point, then both values are equal
		return 0;
	}
	
	// helper function that only subtracts the magnitudes and ignores signs
	private static void subtractWords(DecimalInt larger, DecimalInt smaller, DecimalInt dst) {
		// loop over words
		int carry = 0;
		int word;
		for (int i = 0; i < dst.mData.length; ++i) {
			int sum = carry;
			if (i < larger.mSize)
				sum += (int)larger.mData[i];
			if (i < smaller.mSize)
				sum -= (int)smaller.mData[i];
			
			if (sum > 0) {
				word = sum % 10;
				carry = sum / 10;
			}
			else {
				sum += 10;
				word = sum % 10;
				carry = sum / 10 - 1;
			}
			dst.mData[i] = (byte)word;
			if (word != 0) {
				dst.mSize = i + 1;
			}
		}
	}

	// helper function that only adds the magnitudes and ignores signs
	private static void addWords(DecimalInt larger, DecimalInt smaller, DecimalInt dst) {
		// loop over words
		int carry = 0;
		for (int i = 0; i < dst.mData.length; ++i) {
			int sum = carry;
			if (i < larger.mSize)
				sum += (int)larger.mData[i];
			if (i < smaller.mSize)
				sum += (int)smaller.mData[i];
			
			int word = sum % 10;
			carry = sum / 10;
			dst.mData[i] = (byte)word;
			if (word != 0) {
				dst.mSize = i + 1;
			}
		}
	}

	// helper function for addition and subtraction
	// sign argument should be 1 for addition and -1 for subtraction
	private static DecimalInt addHelper(DecimalInt lhs, DecimalInt rhs, int sign) {
		// determine both values signs
		int lhsSign = lhs.getSign();
		int rhsSign = sign * rhs.getSign();

		// check if one of the two values is zero
		if (rhsSign == 0) {
			DecimalInt result = lhs.copy();
			return result;
		}
		if (lhsSign == 0) {
			DecimalInt result = rhs.copy();
			if (sign < 0 && rhs.getSize() > 0) {
				result.mNegative = !result.mNegative;
			}
			return result;
		}

		int capacity = Math.max(lhs.getSize(), rhs.getSize()) + 1;
		DecimalInt result = new DecimalInt();
		result.grow(capacity);
		int cmp = compareMagnitudes(lhs, rhs);
		
		// check if the sign is equal
		if (lhsSign == rhsSign) {
			// add the words (ignore sign)
			if (cmp > 0) {
				addWords(lhs, rhs, result);
			}
			else {
				addWords(rhs, lhs, result);
			}
			// set the sign of the destination
			if (lhsSign < 0 && rhsSign < 0) {
				result.mNegative = true;
			}
		}
		else {
			// subtract the smaller magnitude from the larger one
			if (cmp > 0) {
				// subtract words (ignoring sign)
				subtractWords(lhs, rhs, result);
				// fix the sign
				if (lhsSign < 0) {
					result.mNegative = true;
				}
			}
			else if (cmp < 0) {
				// subtract words (ignoring sign)
				subtractWords(rhs, lhs, result);
				// fix the sign
				if (rhsSign < 0) {
					result.mNegative = true;
				}
			}
			else {
				// we take this branch, if the signs are different, but the magnitudes are equal
				// therefore the result must be zero, which it already is
				//result.clear();
			}
		}
		return result;
	}

	// add two numbers
	public static DecimalInt add(DecimalInt lhs, DecimalInt rhs) {
		return addHelper(lhs, rhs, 1);
	}

	// subtract two numbers
	public static DecimalInt subtract(DecimalInt lhs, DecimalInt rhs) {
		return addHelper(lhs, rhs, -1);
	}
	
	// multiply two numbers
	public static DecimalInt multiply(DecimalInt lhs, DecimalInt rhs) {
		int capacity = lhs.getSize() + rhs.getSize();
		DecimalInt result = new DecimalInt();
		result.grow(capacity);

		for (int i = 0; i < lhs.getSize(); ++i) {
			for (int j = 0; j < rhs.getSize(); ++j) {
				int carry = lhs.mData[i] * rhs.mData[j];
				for (int k = i + j; k < result.mData.length; ++k) {
					if (carry == 0) {
						break;
					}
					int sum = (int)result.mData[k] + carry;
					byte word = (byte)(sum % 10);
					carry = sum / 10;
					result.mData[k] = word;
					if (word > 0) {
						result.mSize = Math.max(result.mSize, k + 1);
					}
				}
			}
		}

		// set result sign
		result.mNegative = (lhs.mNegative ^ rhs.mNegative);
		
		return result;
	}
	
	// divide two numbers
	// returns an array with two elements. The first element is the quotient, the second is the remainder
	public static DecimalInt[] divide(DecimalInt lhs, DecimalInt rhs) {
		// catch division by zero
		if (rhs.getSign() == 0)
			throw new ArithmeticException("Division by zero");
		
		// handle result 0 separately
		if (DecimalInt.compareMagnitudes(lhs, rhs) < 0) {
			return new DecimalInt[]{new DecimalInt(), lhs};
		}
		
		// initial guess
		// we use an upper estimate of the number of digits of the result and then work downwards
		DecimalInt result = new DecimalInt();
		int maxDigit = lhs.mSize - rhs.mSize + 1;
		result.grow(maxDigit);
		int digit = maxDigit - 1;
		result.mData[digit] = (byte)9;
		result.mSize = digit + 1;

		// we can set the sign without looking at the digits, so do that now
		result.mNegative = lhs.mNegative ^ rhs.mNegative;
		
		// check contains lhs/rhs*rhs, which we compare against lhs again
		// the difference is the remainder of the division
		DecimalInt check = DecimalInt.multiply(rhs, result);
		DecimalInt remainder = DecimalInt.subtract(lhs, check);
		
		// we iterate until the check is equal to lhs
		int cmp;
		while ((cmp = DecimalInt.compareMagnitudes(check, lhs)) != 0) {
			// check if we are too large
			if (cmp > 0) {
				// if we are, reduce the current digit by one
				result.mData[digit]--;
				// check if that made the digit zero
				if (result.mData[digit] == 0) {
					// if that was the last digit, we are done
					if (digit == 0)
						break;
					// other wise, go to the next lowest digit and continue there
					else {
						// if the current digit is also the most significant digit, reduce the size of the data
						if (result.mSize == digit + 1)
							--result.mSize;
						result.mData[--digit] = (byte)9;
					}
				}
			}
			// we went below our threshold
			else {
				// if that was the last digit, we are done
				if (digit == 0)
					break;
				// otherwise, go to the next digit
				else {
					result.mData[--digit] = (byte)9;
				}
			}
			
			check = DecimalInt.multiply(rhs, result);
			remainder = DecimalInt.subtract(lhs, check);
		}

		check = DecimalInt.multiply(rhs, result);
		remainder = DecimalInt.subtract(lhs, check);
		return new DecimalInt[]{result, remainder};
	}
	
	// computes the factorial n!
	public static DecimalInt factorial(int n) {
		DecimalInt result = new DecimalInt(1);
	    for (int i = 1; i <= n; ++i) {
	    	result = DecimalInt.multiply(result, new DecimalInt(i));
	    }
	    return result;
	}
	
	// binomial coefficient "n over k" aka "nCr"
	public static DecimalInt combinations(int n, int k) {
		// exploit symmetry
		if (2*k > n)
			k = n - k;
		
		// initialize with 1
		DecimalInt result = new DecimalInt(1);
		
		for (int i = 1; i <= k; ++i) {
			// result *= n - k + i
			result = DecimalInt.multiply(result, new DecimalInt(n - k + i));
			// result /= i
			result = DecimalInt.divide(result, new DecimalInt(i))[0];
		}
		
		return result;
	}
}
