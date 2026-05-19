package edu.touro.mco152.bm;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SimpleTest {
	@Test
	public void oneSquared() {
		assertTrue(1 * 1 == 1);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 3, 5, -3, 15, Integer.MAX_VALUE })
	public void isOdd(int num) {
		assertTrue(num % 2 != 0);
	}
}
