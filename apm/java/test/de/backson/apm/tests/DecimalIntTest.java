package de.backson.apm.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.util.Formatter;

import org.junit.jupiter.api.Test;

import de.backson.apm.DecimalInt;

class DecimalIntTest {

	long[] data = {
			// a few small numbers
			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
			// a few hex numbers
			0x20, 0xF0, 0xF1, 0xF2, 0xF3, 0xF4, 0xF6, 0xF8, 0xFA, 0xFB, 0xFC, 0xFD, 0xFE, 0xFF,
			// a few large numbers
			0x100, 0x101, 0x10F, 0xF00, 0x10000, 0xFFFF, 0xFFFFFFFF, 0x7FFFFFFFFFFFFFFL,
			// a few negative numbers
			-1, -2, -3, -4, -5, -6, -10, -11, -0xFF, -0x100, -0xFFFFFFFF, -0x7FFFFFFFFFFFFFFL,
			// decimals
			1, 10, 100, 1000, 10000, 100000, 1000000, 10000000,
			1, 11, 101, 1001, 10001, 100001, 1000001, 10000001,
			9, 99, 999, 9999, 99999, 999999, 9999999, 99999999,
			-1, -10, -100, -1000, -10000, -100000, -1000000, -10000000,
			-1, -11, -101, -1001, -10001, -100001, -1000001, -10000001,
			-9, -99, -999, -9999, -99999, -999999, -9999999, -99999999,
			// a few random numbers
			0x0724, 0x7dd5, 0xabd6, 0xbdb2,
			0x1277, 0x2bab, 0x930f, 0x1ae7,
			0x5427, 0x878b, 0x19b0, 0x39b3,
			0x4768, 0x15ba, 0x70ce, 0x1fe2,
			0x9c03, 0x0f33, 0x8c57, 0x9077,
			0x1bbd, 0x403f, 0xe1f9, 0xde52,
			-0x0a62, -0x055d, -0x289f, -0x60f3,
			-0x9559, -0x046a, -0xb48f, -0x40e9,
			-0x89b2, -0x6713, -0xf9a2, -0x63c5,
			-0x1bee, -0x6883, -0x44a3, -0xe258,
			-0xff5b, -0x7895, -0xb81b, -0x929c,
			-0xc0fe, -0xc9e3, -0x41a3, -0x6649,
			0xf57d8427, 0x5e16853c,
			0x0174c820, 0xee3e535b,
			0x2608464f, 0xd6831685,
			0x0f5a7d4a, 0xe9166775,
			0xd5d2f90b, 0x583f18ca,
			0x70fcc575, 0x5568b1ff,
			-0x264d469a, -0xc1b94a79,
			-0x975ccbc3, -0xb70fdfb8,
			-0x0d162453, -0x7ed894f6,
			-0x9bcccdcb, -0x0351e8d5,
			-0x60ccc203, -0x1df29ce5,
			-0xa9c724c9, -0xef562d88,
			// special numbers
			Long.MAX_VALUE, Long.MIN_VALUE,
	};
	
	@Test
	void testConstructFromLong() {
		for (long x : data) {
			testConstructFromLong(x);
		}
	}
	
	private void testConstructFromLong(long l) {
		DecimalInt x = new DecimalInt(l);
		assertEquals(""+l, ""+x);
	}
	
	@Test
	void testEquals() {
		assertEquals(new DecimalInt(0), new DecimalInt(0));
		assertEquals(new DecimalInt(10), new DecimalInt(10));
		assertEquals(new DecimalInt(99), new DecimalInt(99));
	}
	
	@Test
	void testCompare() {
		assertTrue(DecimalInt.compare(new DecimalInt(1), new DecimalInt(0)) > 0);
		assertTrue(DecimalInt.compare(new DecimalInt(0), new DecimalInt(1)) < 0);
		assertTrue(DecimalInt.compare(new DecimalInt(-1), new DecimalInt(0)) < 0);
		assertTrue(DecimalInt.compare(new DecimalInt(0), new DecimalInt(-1)) > 0);
		assertTrue(DecimalInt.compare(new DecimalInt(100), new DecimalInt(10)) > 0);
		assertTrue(DecimalInt.compare(new DecimalInt(-100), new DecimalInt(-10)) < 0);
		assertTrue(DecimalInt.compare(new DecimalInt(-100), new DecimalInt(10)) < 0);
		assertTrue(DecimalInt.compare(new DecimalInt(100), new DecimalInt(-10)) > 0);
	}
	
	@Test
	void testConstructFromStringEqualsLong() {
		assertEquals(new DecimalInt(0), new DecimalInt("0"));
		assertEquals(new DecimalInt(0), new DecimalInt("0000"));
		assertEquals(new DecimalInt(+0), new DecimalInt("+0"));
		assertEquals(new DecimalInt(-0), new DecimalInt("-0"));

		assertEquals(new DecimalInt(99), new DecimalInt("99"));
		assertEquals(new DecimalInt(99), new DecimalInt("0099"));
		assertEquals(new DecimalInt(99), new DecimalInt("+99"));
		assertEquals(new DecimalInt(-99), new DecimalInt("-99"));

		assertEquals(new DecimalInt(16), new DecimalInt("0x10"));
		assertEquals(new DecimalInt(16), new DecimalInt("+0x10"));
		assertEquals(new DecimalInt(-16), new DecimalInt("-0x10"));
		assertEquals(new DecimalInt(255), new DecimalInt("0xFF"));
		assertEquals(new DecimalInt(255), new DecimalInt("0XFF"));
		assertEquals(new DecimalInt(255), new DecimalInt("0xfF"));
		assertEquals(new DecimalInt(255), new DecimalInt("0XFf"));
		assertEquals(new DecimalInt(255), new DecimalInt("0x00FF"));

		assertEquals(new DecimalInt(42), new DecimalInt("0b101010"));
		assertEquals(new DecimalInt(42), new DecimalInt("0B101010"));
		assertEquals(new DecimalInt(42), new DecimalInt("0b0000101010"));
		assertEquals(new DecimalInt(42), new DecimalInt("+0b101010"));
		assertEquals(new DecimalInt(-42), new DecimalInt("-0b101010"));

		for (long x : data) {
			String s = String.format("%d", x);
			assertEquals(new DecimalInt(x), new DecimalInt(s));
		}
		
		for (long x : data) {
			String s = String.format("%x", x);
			assertEquals(new DecimalInt(x), new DecimalInt(""+x));
		}
		
		for (long x : data) {
			String s = String.format("%X", x);
			assertEquals(new DecimalInt(x), new DecimalInt(""+x));
		}

		assertThrows(RuntimeException.class, () -> new DecimalInt(""));
		assertThrows(RuntimeException.class, () -> new DecimalInt("-"));
		assertThrows(RuntimeException.class, () -> new DecimalInt("+"));
		assertThrows(RuntimeException.class, () -> new DecimalInt("0x"));
		assertThrows(RuntimeException.class, () -> new DecimalInt("0X"));
		assertThrows(RuntimeException.class, () -> new DecimalInt("0b"));
		assertThrows(RuntimeException.class, () -> new DecimalInt("0B"));
		assertThrows(RuntimeException.class, () -> new DecimalInt("0-"));
		assertThrows(RuntimeException.class, () -> new DecimalInt("F"));
		assertThrows(RuntimeException.class, () -> new DecimalInt("0xxf"));
		assertThrows(RuntimeException.class, () -> new DecimalInt("0f"));
		assertThrows(RuntimeException.class, () -> new DecimalInt("xf"));
	}
	
	@Test
	void testAdd() {
		for (long x : data)
		for (long y : data) {
			testAdd(x, y);
		}
	}
	
	private void testAdd(long a, long b) {
		try {
			DecimalInt x = new DecimalInt(a);
			DecimalInt y = new DecimalInt(b);
			DecimalInt z = DecimalInt.add(x, y);
			DecimalInt expected = new DecimalInt(Math.addExact(a, b));
			assertEquals(expected, z);
		} catch (ArithmeticException e) {
			// if the operations on the long overflows, skip this test
		}
	}

	@Test
	void testSubtract() {
		for (long x : data)
		for (long y : data) {
			testSubtract(x, y);
		}
	}
	
	private void testSubtract(long a, long b) {
		try {
			DecimalInt x = new DecimalInt(a);
			DecimalInt y = new DecimalInt(b);
			DecimalInt z = DecimalInt.subtract(x, y);
			DecimalInt expected = new DecimalInt(Math.subtractExact(a, b));
			assertEquals(expected, z);
		} catch (ArithmeticException e) {
			// if the operations on the long overflows, ignore the test result
		}
	}

	@Test
	void testMultiply() {
		for (long x : data)
		for (long y : data) {
			testMultiply(x, y);
		}
	}
	
	private void testMultiply(long a, long b) {
		try {
			DecimalInt x = new DecimalInt(a);
			DecimalInt y = new DecimalInt(b);
			DecimalInt z = DecimalInt.multiply(x, y);
			DecimalInt expected = new DecimalInt(Math.multiplyExact(a, b));
			assertEquals(expected, z);
		} catch (ArithmeticException e) {
			// if the operations on the long overflows, ignore the test result
		}
	}

	@Test
	void testDivide() {
		for (long x : data)
		for (long y : data) {
			testDivide(x, y);
		}
	}
	
	private void testDivide(long a, long b) {
		// if we compute Long.MIN_VALUE/-1 the result is -Long.MIN_VALUE, which will underflow
		// this is the only case that won't work with regular longs, so skip that
		if (a == Long.MIN_VALUE && b == -1)
			return;
		
		try {
			DecimalInt x = new DecimalInt(a);
			DecimalInt y = new DecimalInt(b);
			DecimalInt[] z = DecimalInt.divide(x, y);
			DecimalInt expectedValue = new DecimalInt(a/b);
			DecimalInt expectedRemainder = new DecimalInt(a-(a/b)*b);
			assertEquals(expectedValue, z[0], "Incorrect quotient of division "+a+"/"+b);
			assertEquals(expectedRemainder, z[1], "Incorrect remainder of division "+a+"/"+b);
		} catch (ArithmeticException e) {
			// if the operations on the long overflows, ignore the test result
		}
	}
	
	@Test
	void testPowerOfTen() {
		for (int i = 0; i < 12; ++i) {
			testPowerOfTen(i);
		}
	}
	
	private void testPowerOfTen(int exponent) {
		try {
			DecimalInt x = DecimalInt.getPowerOfTen(exponent);
			long power = new BigInteger("10").pow(exponent).longValueExact();
			DecimalInt expected = new DecimalInt(power);
			assertEquals(expected, x);
		} catch (ArithmeticException e) {
			// if the operations on the long overflows, ignore the test result
		}
	}
	
	@Test
	void testFactorial() {
		assertEquals("1", DecimalInt.factorial(0).toString());
		assertEquals("1", DecimalInt.factorial(1).toString());
		assertEquals("2", DecimalInt.factorial(2).toString());
		assertEquals("6", DecimalInt.factorial(3).toString());
		assertEquals("24", DecimalInt.factorial(4).toString());
		assertEquals("120", DecimalInt.factorial(5).toString());
		assertEquals("3628800", DecimalInt.factorial(10).toString());
		assertEquals("2432902008176640000", DecimalInt.factorial(20).toString());
		assertEquals("815915283247897734345611269596115894272000000000", DecimalInt.factorial(40).toString());
		assertEquals("933262154439441526816992388562667004907159682643816214685929638952175999932299156089414639761565182862536979208272237582511852109168640000000000000000000000", DecimalInt.factorial(99).toString());
	}
	
	@Test
	void testCombinations() {
		assertEquals("1", DecimalInt.combinations(0, 0).toString());
		assertEquals("1", DecimalInt.combinations(1, 0).toString());
		assertEquals("1", DecimalInt.combinations(2, 0).toString());
		assertEquals("1", DecimalInt.combinations(3, 0).toString());
		assertEquals("1", DecimalInt.combinations(10, 0).toString());
		assertEquals("1", DecimalInt.combinations(20, 0).toString());
		assertEquals("1", DecimalInt.combinations(100, 0).toString());
		
		assertEquals("1", DecimalInt.combinations(1, 1).toString());
		assertEquals("1", DecimalInt.combinations(2, 2).toString());
		assertEquals("1", DecimalInt.combinations(3, 3).toString());
		assertEquals("1", DecimalInt.combinations(10, 10).toString());
		assertEquals("1", DecimalInt.combinations(20, 20).toString());
		assertEquals("1", DecimalInt.combinations(100, 100).toString());

		assertEquals("2", DecimalInt.combinations(2, 1).toString());
		assertEquals("6", DecimalInt.combinations(4, 2).toString());
		assertEquals("10", DecimalInt.combinations(5, 2).toString());
		assertEquals("20", DecimalInt.combinations(6, 3).toString());
		assertEquals("4027810484880", DecimalInt.combinations(65, 12).toString());
	}
}
