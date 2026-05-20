package com.shatteredpixel.shatteredpixeldungeon.stats;

public class CombatStats {
    public int totalDamageDealt = 0;
    public int totalAttacksAttempted = 0;
    public int totalAttacksHit = 0;
    public int totalAttacksEvaded = 0;
    public int totalKills = 0;
    public int totalDamageTaken = 0;

    public float getAccuracyRate() {
        if (totalAttacksAttempted == 0) return 0f;
        return (float) totalAttacksHit / totalAttacksAttempted;
    }

    public float getEvadeRate() {
        // You can calculate this based on enemy attempts vs player dodges
        return 0f; 
    }

    public void reset() {
        totalDamageDealt = 0;
        totalAttacksAttempted = 0;
        totalAttacksHit = 0;
        totalAttacksEvaded = 0;
        totalKills = 0; 
        totalDamageTaken = 0; 
    }
}