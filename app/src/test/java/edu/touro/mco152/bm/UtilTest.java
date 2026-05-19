package edu.touro.mco152.bm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Util.randInt() and Util.displayString().
 * These are simple helper methods with no side effects so they're easy to unit test.
 */
public class UtilTest {

    // --- displayString ---

    /**
     * Right-BICEP: Right - checks that displayString() gives back the right
     * formatted string for a few different inputs. Uses @ParameterizedTest
     * to avoid writing the same test three times.
     * The format is "###.##" so it rounds to 2 decimal places and drops trailing zeros.
     */
    @ParameterizedTest
    @CsvSource({
            "1.5,     1.5",
            "100.0,   100",
            "1234.567, 1234.57"
    })
    void displayString_CorrectOutput(double input, String expected) {
        assertEquals(expected.trim(), Util.displayString(input),
                "displayString(" + input + ") should return " + expected.trim());
    }

    // --- randInt ---

    /**
     * Right-BICEP: Boundary - runs randInt 1000 times and checks that every
     * result is actually between min and max inclusive. If the formula is wrong
     * it would sometimes go out of range.
     *
     * CORRECT Boundary: Range - every result must be within [min, max]. Anything
     * outside that range would be a bug.
     */
    @Test
    void randInt_ResultAlwaysWithinRange() {
        int min = 10, max = 20;
        for (int i = 0; i < 1000; i++) {
            int result = Util.randInt(min, max);
            assertTrue(result >= min && result <= max,
                    "randInt(" + min + "," + max + ") returned " + result + " which is out of range");
        }
    }

    /**
     * Right-BICEP: Boundary - tests the edge case where min and max are the same.
     * There's only one possible answer so randInt should always return that value.
     *
     * CORRECT Boundary: Ordering - when min == max the range shrinks down to a
     * single value, so the result has to be exactly that value every time.
     */
    @Test
    void randInt_MinEqualsMax_AlwaysReturnsMin() {
        for (int i = 0; i < 50; i++) {
            assertEquals(7, Util.randInt(7, 7),
                    "randInt(7, 7) should always return 7 since min and max are the same");
        }
    }

    /**
     * Right-BICEP: Error - if min > max the math inside randInt breaks
     * (a negative bound gets passed to Random.nextInt) and it should throw.
     * This test verifies that the exception actually happens.
     */
    @Test
    void randInt_MinGreaterThanMax_ThrowsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> Util.randInt(10, 5),
                "randInt(10, 5) should throw because min > max causes a negative nextInt bound");
    }
}
