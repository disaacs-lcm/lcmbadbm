package edu.touro.mco152.bm;

import edu.touro.mco152.bm.persist.DiskRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DiskRun's display methods: getMin(), getMax(), getDuration(), toString().
 * Only methods that don't need a database connection are tested here.
 */
public class DiskRunTest {

    private DiskRun run;

    @BeforeEach
    void setUp() {
        run = new DiskRun(DiskRun.IOMode.WRITE, DiskRun.BlockSequence.SEQUENTIAL);
    }

    // --- getMin / getMax ---

    /**
     * Right-BICEP: Right - after setting a positive min value, getMin() should
     * return it formatted as a string. Just checking the basic correct-result case.
     */
    @Test
    void getMin_ReturnsFormattedValueWhenPositive() {
        run.setMin(123.45);
        assertEquals("123.45", run.getMin(),
                "getMin() should format and return the stored runMin value");
    }

    /**
     * Right-BICEP: Right - when runMax is -1, getMax() should return "- -" instead
     * of formatting the number. The app uses -1 to mean "no data recorded yet."
     *
     * CORRECT Boundary: Range - -1 is the sentinel at the bottom of the range that
     * means "no measurement." Only exactly -1 triggers the "- -" display.
     */
    @Test
    void getMax_ReturnsDashForNegativeOne() {
        run.setMax(-1);
        assertEquals("- -", run.getMax(),
                "getMax() should return \"- -\" when runMax is -1 (no data sentinel)");
    }

    // --- getDuration ---

    /**
     * Right-BICEP: Boundary - if endTime was never set it stays null.
     * getDuration() should handle that gracefully and return "unknown"
     * instead of throwing a NullPointerException.
     *
     * CORRECT Boundary: Existence - endTime might not exist (null) if the run
     * never finished. The method has to deal with that case.
     */
    @Test
    void getDuration_ReturnsUnknownWhenEndTimeIsNull() {
        // endTime is null by default, we never set it
        assertEquals("unknown", run.getDuration(),
                "getDuration() should return \"unknown\" when endTime hasn't been set");
    }

    // --- toString ---

    /**
     * Right-BICEP: Cross-check - verifies toString() by checking that it
     * contains the IOMode and BlockSequence passed to the constructor.
     * This is a cross-check because the output is confirmed using information
     * already known from a different source (the constructor args).
     */
    @Test
    void toString_ContainsIoModeAndBlockOrder() {
        String str = run.toString();
        assertTrue(str.contains("WRITE"),
                "toString() should include WRITE since that's the IOMode we set; got: " + str);
        assertTrue(str.contains("SEQUENTIAL"),
                "toString() should include SEQUENTIAL since that's the block order we set; got: " + str);
    }
}
