package org.example.ui;

import java.util.Scanner;

public class Input {
    private final Scanner scanner = new Scanner(System.in);

    public int readChoice() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();
            return choice;
        } catch (Exception e) {
            scanner.nextLine();
            return 0;
        }
    }

    public String readText() {
        return scanner.nextLine();
    }

    public void close() {
        scanner.close();
    }
}
