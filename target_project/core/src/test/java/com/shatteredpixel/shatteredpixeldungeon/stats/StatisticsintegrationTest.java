package com.shatteredpixel.shatteredpixeldungeon.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the combat statistics system.
 *
 * StatisticsManager calls GameScene.updateStatsDisplay(), which requires the
 * full game runtime. We stub that static call with Mockito so the stat logic
 * can be exercised in isolation.
 *
 * Static mocking requires the inline mock maker. Create this file in your test resources:
 *   core/src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker
 * containing just one line:
 *   mock-maker-inline
 */
@ExtendWith(MockitoExtension.class)
class StatisticsIntegrationTest {

    @BeforeEach
    void resetStatsBetweenTests() {
        // StatisticsManager holds a static singleton — reset before each test
        // so tests are fully independent of execution order.
        StatisticsManager.reset();
    }


    // -----------------------------------------------------------------------
    // CombatStats — pure logic, no GameScene dependency
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("CombatStats")
    class CombatStatsTests {

        @Test
        @DisplayName("accuracyRate is 0 when no attacks have been attempted")
        void accuracyRate_isZero_whenNoAttemptsYet() {
            CombatStats stats = new CombatStats();
            assertEquals(0f, stats.getAccuracyRate(), 0.001f);
        }

        @Test
        @DisplayName("accuracyRate is 1.0 when every attack hits")
        void accuracyRate_isOne_whenEveryAttackHits() {
            CombatStats stats = new CombatStats();
            stats.totalAttacksAttempted = 5;
            stats.totalAttacksHit = 5;
            assertEquals(1.0f, stats.getAccuracyRate(), 0.001f);
        }

        @Test
        @DisplayName("accuracyRate is 0.5 when half of attacks hit")
        void accuracyRate_isHalf_whenHalfAttacksHit() {
            CombatStats stats = new CombatStats();
            stats.totalAttacksAttempted = 10;
            stats.totalAttacksHit = 5;
            assertEquals(0.5f, stats.getAccuracyRate(), 0.001f);
        }

        @Test
        @DisplayName("reset() clears every field to zero")
        void reset_clearsAllFields() {
            CombatStats stats = new CombatStats();
            stats.totalDamageDealt = 100;
            stats.totalAttacksAttempted = 20;
            stats.totalAttacksHit = 15;
            stats.totalKills = 4;
            stats.totalDamageTaken = 50;

            stats.reset();

            assertAll(
                () -> assertEquals(0, stats.totalDamageDealt),
                () -> assertEquals(0, stats.totalAttacksAttempted),
                () -> assertEquals(0, stats.totalAttacksHit),
                () -> assertEquals(0, stats.totalKills),
                () -> assertEquals(0, stats.totalDamageTaken)
            );
        }
    }


    // -----------------------------------------------------------------------
    // StatisticsManager — GameScene is stubbed via mockito-inline
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("StatisticsManager - trackAttack")
    class TrackAttackTests {

        @Test
        @DisplayName("hit: increments attempts, hits, and damage dealt")
        void trackAttack_hit_incrementsHitAndDamage() {
            try (MockedStatic<GameScene> gs = Mockito.mockStatic(GameScene.class)) {
                StatisticsManager.trackAttack(true, 42);

                CombatStats stats = StatisticsManager.getCombatStats();
                assertAll(
                    () -> assertEquals(1, stats.totalAttacksAttempted),
                    () -> assertEquals(1, stats.totalAttacksHit),
                    () -> assertEquals(42, stats.totalDamageDealt)
                );
            }
        }

        @Test
        @DisplayName("miss: increments attempts only, damage stays 0")
        void trackAttack_miss_incrementsAttemptedOnly() {
            try (MockedStatic<GameScene> gs = Mockito.mockStatic(GameScene.class)) {
                StatisticsManager.trackAttack(false, 0);

                CombatStats stats = StatisticsManager.getCombatStats();
                assertAll(
                    () -> assertEquals(1, stats.totalAttacksAttempted),
                    () -> assertEquals(0, stats.totalAttacksHit),
                    () -> assertEquals(0, stats.totalDamageDealt)
                );
            }
        }

        @Test
        @DisplayName("miss: damage is ignored even if caller passes a non-zero value")
        void trackAttack_damageIgnored_whenMiss() {
            try (MockedStatic<GameScene> gs = Mockito.mockStatic(GameScene.class)) {
                StatisticsManager.trackAttack(false, 99);
                assertEquals(0, StatisticsManager.getCombatStats().totalDamageDealt);
            }
        }

        @Test
        @DisplayName("damage accumulates correctly across multiple hits")
        void trackAttack_damageAccumulates_acrossMultipleHits() {
            try (MockedStatic<GameScene> gs = Mockito.mockStatic(GameScene.class)) {
                StatisticsManager.trackAttack(true, 10);
                StatisticsManager.trackAttack(true, 25);
                StatisticsManager.trackAttack(true, 5);
                assertEquals(40, StatisticsManager.getCombatStats().totalDamageDealt);
            }
        }
    }

    @Nested
    @DisplayName("StatisticsManager - trackKill / trackDamageTaken")
    class TrackOtherTests {

        @Test
        @DisplayName("trackKill increments kill count")
        void trackKill_incrementsKillCount() {
            try (MockedStatic<GameScene> gs = Mockito.mockStatic(GameScene.class)) {
                StatisticsManager.trackKill();
                StatisticsManager.trackKill();
                assertEquals(2, StatisticsManager.getCombatStats().totalKills);
            }
        }

        @Test
        @DisplayName("trackDamageTaken accumulates across multiple calls")
        void trackDamageTaken_accumulatesCorrectly() {
            try (MockedStatic<GameScene> gs = Mockito.mockStatic(GameScene.class)) {
                StatisticsManager.trackDamageTaken(10);
                StatisticsManager.trackDamageTaken(25);
                assertEquals(35, StatisticsManager.getCombatStats().totalDamageTaken);
            }
        }

        @Test
        @DisplayName("reset() clears all stats held by the manager")
        void reset_clearsAllStatsOnManager() {
            try (MockedStatic<GameScene> gs = Mockito.mockStatic(GameScene.class)) {
                StatisticsManager.trackAttack(true, 50);
                StatisticsManager.trackKill();
                StatisticsManager.trackDamageTaken(20);

                StatisticsManager.reset();

                CombatStats stats = StatisticsManager.getCombatStats();
                assertAll(
                    () -> assertEquals(0, stats.totalAttacksAttempted),
                    () -> assertEquals(0, stats.totalAttacksHit),
                    () -> assertEquals(0, stats.totalDamageDealt),
                    () -> assertEquals(0, stats.totalKills),
                    () -> assertEquals(0, stats.totalDamageTaken)
                );
            }
        }
    }


    // -----------------------------------------------------------------------
    // GameScene.updateStatsDisplay() call-count contracts
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("GameScene.updateStatsDisplay() invocation contracts")
    class DisplayUpdateTests {

        @Test
        @DisplayName("called once on a hit")
        void calledOnce_onHit() {
            try (MockedStatic<GameScene> gs = Mockito.mockStatic(GameScene.class)) {
                StatisticsManager.trackAttack(true, 10);
                gs.verify(GameScene::updateStatsDisplay, Mockito.times(1));
            }
        }

        @Test
        @DisplayName("called once on a miss")
        void calledOnce_onMiss() {
            try (MockedStatic<GameScene> gs = Mockito.mockStatic(GameScene.class)) {
                StatisticsManager.trackAttack(false, 0);
                gs.verify(GameScene::updateStatsDisplay, Mockito.times(1));
            }
        }

        @Test
        @DisplayName("called once on trackKill")
        void calledOnce_onKill() {
            try (MockedStatic<GameScene> gs = Mockito.mockStatic(GameScene.class)) {
                StatisticsManager.trackKill();
                gs.verify(GameScene::updateStatsDisplay, Mockito.times(1));
            }
        }

        @Test
        @DisplayName("called once on trackDamageTaken")
        void calledOnce_onDamageTaken() {
            try (MockedStatic<GameScene> gs = Mockito.mockStatic(GameScene.class)) {
                StatisticsManager.trackDamageTaken(5);
                gs.verify(GameScene::updateStatsDisplay, Mockito.times(1));
            }
        }
    }


    // -----------------------------------------------------------------------
    // Multi-step scenario tests — simulate real combat sequences
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("Combat scenarios")
    class CombatScenarioTests {

        @Test
        @DisplayName("60% accuracy after 3 hits and 2 misses")
        void accuracyCalculatesCorrectly_afterMixedAttacks() {
            try (MockedStatic<GameScene> gs = Mockito.mockStatic(GameScene.class)) {
                StatisticsManager.trackAttack(true, 10);
                StatisticsManager.trackAttack(true, 15);
                StatisticsManager.trackAttack(false, 0);
                StatisticsManager.trackAttack(true, 20);
                StatisticsManager.trackAttack(false, 0);

                CombatStats stats = StatisticsManager.getCombatStats();
                assertAll(
                    () -> assertEquals(5, stats.totalAttacksAttempted),
                    () -> assertEquals(3, stats.totalAttacksHit),
                    () -> assertEquals(45, stats.totalDamageDealt),
                    () -> assertEquals(0.6f, stats.getAccuracyRate(), 0.001f)
                );
            }
        }

        @Test
        @DisplayName("stats are fully clean after reset between runs")
        void fullRunThenReset_statsAreClean() {
            try (MockedStatic<GameScene> gs = Mockito.mockStatic(GameScene.class)) {
                StatisticsManager.trackAttack(true, 30);
                StatisticsManager.trackKill();
                StatisticsManager.trackDamageTaken(15);

                StatisticsManager.reset();

                CombatStats stats = StatisticsManager.getCombatStats();
                assertAll(
                    () -> assertEquals(0f, stats.getAccuracyRate(), 0.001f),
                    () -> assertEquals(0, stats.totalKills),
                    () -> assertEquals(0, stats.totalDamageTaken)
                );
            }
        }

        @Test
        @DisplayName("float accuracy and integer display percentage agree at 50%")
        void displayValuesMatchInternalStats() {
            // Verifies the integer-percentage formula used by GameScene.updateStatsDisplay()
            // matches the float rate from CombatStats.getAccuracyRate().
            try (MockedStatic<GameScene> gs = Mockito.mockStatic(GameScene.class)) {
                StatisticsManager.trackAttack(true, 20);
                StatisticsManager.trackAttack(true, 20);
                StatisticsManager.trackAttack(false, 0);
                StatisticsManager.trackAttack(false, 0);

                CombatStats stats = StatisticsManager.getCombatStats();

                float floatAccuracy = stats.getAccuracyRate();
                int displayedPercent = stats.totalAttacksAttempted > 0
                        ? (int)(100f * stats.totalAttacksHit / stats.totalAttacksAttempted)
                        : 0;

                assertAll(
                    () -> assertEquals(0.5f, floatAccuracy, 0.001f),
                    () -> assertEquals(50, displayedPercent)
                );
            }
        }
    }
}