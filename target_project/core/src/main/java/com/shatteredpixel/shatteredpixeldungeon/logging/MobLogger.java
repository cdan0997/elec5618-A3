package com.shatteredpixel.shatteredpixeldungeon.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;



public class MobLogger {

    private static final String LOG_FILE = "mob_log.txt";
    private static int logCount = 0;

    public static void log(String message) {

        long startTime = System.nanoTime();

        try {
            FileWriter fw = new FileWriter(LOG_FILE, true);
            PrintWriter pw = new PrintWriter(fw);

            pw.println(message);
            pw.close();

            logCount++;

            long endTime = System.nanoTime();
            long duration = endTime - startTime;

            System.out.println("Log count: " + logCount);
            System.out.println("Log write time: " + duration + " ns");

        } catch (IOException e) {
            System.out.println("Logging failed: " + e.getMessage());
        }
    }

    public static int getLogCount() {
        return logCount;
    }
}