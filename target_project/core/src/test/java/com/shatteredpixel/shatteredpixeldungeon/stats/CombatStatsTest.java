package com.shatteredpixel.shatteredpixeldungeon.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CombatStatsTest {

    private CombatStats stats;

    @BeforeEach
    void setUp() {
        stats = new CombatStats();
    }

    // ── Initial state ─────────────────────────────────────────────────────────

    @Test
    void initialState_allCountersAreZero() {
        assertEquals(0, stats.totalDamageDealt);
        assertEquals(0, stats.totalAttacksAttempted);
        assertEquals(0, stats.totalAttacksHit);
        assertEquals(0, stats.totalKills);
        assertEquals(0, stats.totalDamageTaken);
    }

    @Test
    void initialState_accuracyRateIsZero() {
        assertEquals(0f, stats.getAccuracyRate(), 0.001f);
    }

    // ── getAccuracyRate ───────────────────────────────────────────────────────

    @Test
    void accuracyRate_allHits_returnsOne() {
        stats.totalAttacksAttempted = 5;
        stats.totalAttacksHit = 5;
        assertEquals(1.0f, stats.getAccuracyRate(), 0.001f);
    }

    @Test
    void accuracyRate_halfHits_returnsHalf() {
        stats.totalAttacksAttempted = 10;
        stats.totalAttacksHit = 5;
        assertEquals(0.5f, stats.getAccuracyRate(), 0.001f);
    }

    @Test
    void accuracyRate_noHits_returnsZero() {
        stats.totalAttacksAttempted = 10;
        stats.totalAttacksHit = 0;
        assertEquals(0f, stats.getAccuracyRate(), 0.001f);
    }

    @Test
    void accuracyRate_noAttemptsAtAll_returnsZeroNotNaN() {
        // Verifies division-by-zero guard
        float rate = stats.getAccuracyRate();
        assertFalse(Float.isNaN(rate));
        assertEquals(0f, rate, 0.001f);
    }

    @Test
    void accuracyRate_singleAttemptHit_returnsOne() {
        stats.totalAttacksAttempted = 1;
        stats.totalAttacksHit = 1;
        assertEquals(1.0f, stats.getAccuracyRate(), 0.001f);
    }

    @Test
    void accuracyRate_singleAttemptMiss_returnsZero() {
        stats.totalAttacksAttempted = 1;
        stats.totalAttacksHit = 0;
        assertEquals(0f, stats.getAccuracyRate(), 0.001f);
    }

    // ── reset ─────────────────────────────────────────────────────────────────

    @Test
    void reset_clearsAllCounters() {
        stats.totalDamageDealt     = 100;
        stats.totalAttacksAttempted = 20;
        stats.totalAttacksHit      = 15;
        stats.totalKills           = 7;
        stats.totalDamageTaken     = 50;

        stats.reset();

        assertEquals(0, stats.totalDamageDealt);
        assertEquals(0, stats.totalAttacksAttempted);
        assertEquals(0, stats.totalAttacksHit);
        assertEquals(0, stats.totalKills);
        assertEquals(0, stats.totalDamageTaken);
    }

    @Test
    void reset_afterReset_accuracyRateIsZero() {
        stats.totalAttacksAttempted = 10;
        stats.totalAttacksHit = 8;

        stats.reset();

        assertEquals(0f, stats.getAccuracyRate(), 0.001f);
    }

    // ── Counter accumulation ──────────────────────────────────────────────────

    @Test
    void counters_accumulateCorrectly() {
        stats.totalDamageDealt += 30;
        stats.totalDamageDealt += 20;
        assertEquals(50, stats.totalDamageDealt);
    }

    @Test
    void kills_incrementIndependentlyFromDamage() {
        stats.totalKills = 3;
        stats.totalDamageDealt = 200;

        assertEquals(3, stats.totalKills);
        assertEquals(200, stats.totalDamageDealt);
    }
}
