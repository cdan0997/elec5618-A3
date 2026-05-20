package com.shatteredpixel.shatteredpixeldungeon.stats;

import com.shatteredpixel.shatteredpixeldungeon.stats.CombatStats;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;

public class StatisticsManager {
    
    private static final CombatStats currentRunStats = new CombatStats();

    public static CombatStats getCombatStats() {
        return currentRunStats;
    }

    public static void trackAttack(boolean isHit, int damage) {
        currentRunStats.totalAttacksAttempted++;
        if (isHit) {
            currentRunStats.totalAttacksHit++;
            currentRunStats.totalDamageDealt += damage;
        }
        GameScene.updateStatsDisplay();

    }

    public static void trackEvade() {
        currentRunStats.totalAttacksEvaded++;
    }
    
    public static void reset() {
        currentRunStats.reset();
    }
    
    public static void trackKill() {
        currentRunStats.totalKills++;
        GameScene.updateStatsDisplay();

    }

    public static void trackDamageTaken(int damage) {
        currentRunStats.totalDamageTaken += damage;
        GameScene.updateStatsDisplay();

    }
}
