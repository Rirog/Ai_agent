package org.example;


import org.example.config.ConfigManager;
import org.example.startTest.AiCreate;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        AiCreate aiCreate = new AiCreate();
        aiCreate.createAi();

        String model = ConfigManager.getAiModelNew();

        Scanner scanner = new Scanner(System.in);
        AiModelRequest aiModelRequest = new AiModelRequest();

        String menu = """
                \nИИ агент тест
                0 -> Выйти
                1 -> Написать другу
                2 -> Общаться с лучшим другом
                """;
        String podMenu = """
                0 -> Выйти
                1 -> писать дальше
                """;
        int choice;
        do {
            System.out.println(menu);

            choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1: {
                    String prompt = scanner.nextLine();

                    aiModelRequest.getAiResponse(model, prompt);
                    break;
                }
                case 2: {
                    int podChoice = 1;
                    while (podChoice == 1) {

                        String prompt = scanner.nextLine();

                        aiModelRequest.getChatAiResponse(model, prompt);

                        System.out.println(podMenu);
                        podChoice = scanner.nextInt();
                        scanner.nextLine();
                    }
                    break;
                }
            }

        } while (choice != 0);
        scanner.close();
    }
}