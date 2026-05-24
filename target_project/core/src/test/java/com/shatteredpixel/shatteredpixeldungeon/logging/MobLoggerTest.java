package com.shatteredpixel.shatteredpixeldungeon.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MobLogger.
 *
 * NOTE: SpotBugs flags a resource leak in MobLogger.log() — FileWriter and
 * PrintWriter are not wrapped in try-with-resources, so streams can leak if
 * pw.println() throws. See SpotBugs report: OBL_UNSATISFIED_OBLIGATION.
 * This is a known issue to be fixed in a follow-up commit.
 */
class MobLoggerTest {

    private static final String LOG_FILE = "mob_log.txt";

    @BeforeEach
    void resetLogCount() throws Exception {
        // Reset static logCount between tests via reflection
        Field f = MobLogger.class.getDeclaredField("logCount");
        f.setAccessible(true);
        f.set(null, 0);
        // Remove stale log file
        new File(LOG_FILE).delete();
    }

    @AfterEach
    void cleanUp() {
        new File(LOG_FILE).delete();
    }

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    void initialLogCount_isZero() {
        assertEquals(0, MobLogger.getLogCount());
    }

    // ── log() increments counter ──────────────────────────────────────────────

    @Test
    void log_singleCall_incrementsCountToOne() {
        MobLogger.log("MOB_SPAWN | id=1 | Rat");
        assertEquals(1, MobLogger.getLogCount());
    }

    @Test
    void log_multipleCalls_accumulatesCount() {
        MobLogger.log("MOB_SPAWN | id=1 | Rat");
        MobLogger.log("STATE_CHANGE | id=1 | SLEEPING -> HUNTING");
        MobLogger.log("ALERT | id=1 | target=Hero");
        assertEquals(3, MobLogger.getLogCount());
    }

    // ── log() writes to file ──────────────────────────────────────────────────

    @Test
    void log_createsLogFile() {
        MobLogger.log("test message");
        assertTrue(new File(LOG_FILE).exists(), "Log file should be created");
    }

    @Test
    void log_writesMessageToFile() throws Exception {
        String message = "MOB_SPAWN | id=42 | Gnoll";
        MobLogger.log(message);

        String content = new String(Files.readAllBytes(new File(LOG_FILE).toPath()));
        assertTrue(content.contains(message), "Log file should contain the logged message");
    }

    @Test
    void log_appendsMultipleMessages() throws Exception {
        MobLogger.log("first");
        MobLogger.log("second");

        String content = new String(Files.readAllBytes(new File(LOG_FILE).toPath()));
        assertTrue(content.contains("first"));
        assertTrue(content.contains("second"));
    }

    // ── log() does not throw on repeated calls ────────────────────────────────

    @Test
    void log_doesNotThrow() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                MobLogger.log("event " + i);
            }
        });
    }
}
