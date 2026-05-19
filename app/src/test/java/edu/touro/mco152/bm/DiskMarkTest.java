package edu.touro.mco152.bm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the DiskMark class.
 * DiskMark stores the bandwidth and stats for one mark in a benchmark run.
 */
public class DiskMarkTest {

    private DiskMark rMark;
    private DiskMark wMark;

    @BeforeEach
    void setUp() {
        rMark = new DiskMark(DiskMark.MarkType.READ);
        wMark = new DiskMark(DiskMark.MarkType.WRITE);
    }

    // --- setMarkNum / getMarkNum ---

    /**
     * Right-BICEP: Right - makes sure getMarkNum() gives back whatever was put in
     * with setMarkNum(). Tests a few values including 0 and MAX_VALUE.
     *
     * CORRECT Boundary: Ordering - 0 is the lowest possible mark number and
     * Integer.MAX_VALUE is the highest, so this checks both ends of the range.
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 25, Integer.MAX_VALUE})
    void markNum_ReturnsWhatWasSet(int num) {
        wMark.setMarkNum(num);
        assertEquals(num, wMark.getMarkNum(),
                "getMarkNum() should return the same value that was passed to setMarkNum()");
    }

    // --- setBwMbSec / getBwMbSec ---

    /**
     * Right-BICEP: Right - checks that getBwMbSec() returns the right value
     * after setBwMbSec() is called. Basic getter/setter correctness test.
     */
    @Test
    void bwMbSec_ReturnsCorrectValue() {
        wMark.setBwMbSec(50.0);
        assertEquals(50.0, wMark.getBwMbSec(), 0.0001,
                "getBwMbSec() should return the value that was set");
    }

    /**
     * Right-BICEP: Boundary - testing that 0.0 works as a bandwidth value.
     * Zero is the lowest meaningful value since you can't have negative MB/s.
     *
     * CORRECT Boundary: Range - 0.0 is the bottom of the valid range for bandwidth.
     */
    @Test
    void bwMbSec_ZeroIsValidLowerBoundary() {
        wMark.setBwMbSec(0.0);
        assertEquals(0.0, wMark.getBwMbSec(), 0.0,
                "0.0 should be a valid bandwidth value (lower boundary of the range)");
    }

    // --- getBwMbSecAsString ---

    /**
     * Right-BICEP: Cross-check - verifies getBwMbSecAsString() by computing
     * what the answer should be using DecimalFormat independently, then comparing.
     * If the method uses a different format or wrong value, this will catch it.
     *
     * NOTE: This test is intentionally broken for the JunitFirstPass submission.
     * See the comment in DiskMark.getBwMbSecAsString() for the induced error.
     */
    @Test
    void bwMbSecAsString_CrossCheckWithDecimalFormat() {
        wMark.setBwMbSec(1234.5678);
        DecimalFormat df = new DecimalFormat("###.###");
        String expected = df.format(1234.5678);
        assertEquals(expected, wMark.getBwMbSecAsString(),
                "getBwMbSecAsString() should match what DecimalFormat(\"###.###\") produces");
    }

    /**
     * Right-BICEP: Boundary - checking that the string looks like an actual number.
     * It should be digits, maybe a dot, then more digits - not something random.
     *
     * CORRECT Boundary: Conformance - the output has to conform to a numeric format
     * since it gets displayed in the UI.
     */
    @Test
    void bwMbSecAsString_ConformsToNumericFormat() {
        wMark.setBwMbSec(99.9);
        String result = wMark.getBwMbSecAsString();
        assertTrue(result.matches("\\d+(\\.\\d+)?"),
                "getBwMbSecAsString() should look like a number, but got: " + result);
    }

    /**
     * Right-BICEP: Boundary - making sure getBwMbSecAsString() never returns null,
     * even on a brand new DiskMark where bwMbSec is still 0.
     *
     * CORRECT Boundary: Existence - the string has to exist (not null) or the UI
     * would crash trying to display it.
     */
    @Test
    void bwMbSecAsString_NeverNull() {
        // rMark is freshly created so bwMbSec is still 0.0
        assertNotNull(rMark.getBwMbSecAsString(),
                "getBwMbSecAsString() should never return null");
    }

    /**
     * Right-BICEP: Performance - calling getBwMbSecAsString() 100,000 times
     * should finish in under 1 second. It gets called a lot during a benchmark run
     * so it needs to be fast.
     */
    @Test
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    void getBwMbSecAsString_CompletesWithinTimeLimit() {
        wMark.setBwMbSec(512.345);
        for (int i = 0; i < 100_000; i++) {
            wMark.getBwMbSecAsString();
        }
        // reaching here within the time limit means performance is fine
    }

    // --- setCumAvg / getCumAvg ---

    /**
     * Right-BICEP: Error - testing that a negative cumAvg gets stored as-is.
     * The app uses -1 as a "no data yet" sentinel so the field has to hold
     * negative values without changing them.
     */
    @Test
    void cumAvg_NegativeValueIsStoredAsGiven() {
        wMark.setCumAvg(-1.0);
        assertEquals(-1.0, wMark.getCumAvg(), 0.0001,
                "setCumAvg(-1.0) should store -1.0 exactly since it's used as a sentinel value");
    }
}
