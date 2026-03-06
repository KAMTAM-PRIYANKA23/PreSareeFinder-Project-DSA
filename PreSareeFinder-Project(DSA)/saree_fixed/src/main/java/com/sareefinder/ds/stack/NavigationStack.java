package com.sareefinder.ds.stack;

/**
 * NAVIGATION HISTORY STACK — CO3
 * ================================
 * Tracks which menu screens the user has visited.
 * Pressing "Back" pops the stack to return to the previous screen.
 *
 * BIG-O: push O(1), pop O(1), peek O(1)
 */
public class NavigationStack {

    private static final int MAX = 20;
    private String[] history = new String[MAX];
    private int      top     = -1;

    public void push(String screenName) {
        if (top < MAX - 1) history[++top] = screenName;
    }

    public String pop() {
        if (top < 0) return "MAIN_MENU";
        return history[top--];
    }

    public String peek() {
        return top < 0 ? "MAIN_MENU" : history[top];
    }

    public boolean isEmpty() { return top < 0; }

    public void displayHistory() {
        if (isEmpty()) { System.out.println("  No navigation history."); return; }
        System.out.println("  Navigation trail:");
        for (int i = 0; i <= top; i++) {
            System.out.println("    " + (i + 1) + ". " + history[i]);
        }
    }
}
