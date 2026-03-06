package com.sareefinder.util;

/**
 * DISPLAY UTILITIES
 * =================
 * Shared formatting and display helpers for the console UI.
 */
public class DisplayUtil {

    public static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════════════╗");
        System.out.println("  ║         🪷  HYDERABAD SAREE FINDER SYSTEM  🪷            ║");
        System.out.println("  ║      Java DSA Console Application — Academic Project     ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    public static void printDivider(String title) {
        int pad = Math.max(0, 55 - title.length());
        String line = "  ══ " + title + " " + "═".repeat(pad);
        System.out.println(line);
    }

    public static void printLine() {
        System.out.println("  " + "─".repeat(58));
    }

    public static void printSuccess(String msg) {
        System.out.println("  ✓ " + msg);
    }

    public static void printError(String msg) {
        System.out.println("  ✗ " + msg);
    }

    public static void printInfo(String msg) {
        System.out.println("  ℹ " + msg);
    }

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public static void printDSANote(String note) {
        System.out.println("  [DSA] " + note);
    }
}
