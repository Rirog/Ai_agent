package org.example.ui;


public class UserInterface {

    public void getMenu() {
        String menu = """
                \nИИ агент тест
                0 -> Выйти
                1 -> Написать другу
                """;
        outputMessage(menu);
    }

    public void email() {
        String email = "Email: ";
        outputMessage(email);
    }

    public void appPassword() {
        String password = "App password: ";
        outputMessage(password);
    }

    public void outputErrorChoice() {
        String errorChoice = "Такой команды нету";
        outputMessage(errorChoice);
    }

    public void outputErrorEmail() {
        String errorChoice = "Неизвестный тип";
        outputMessage(errorChoice);
    }

    public void outputMessage(String message) {
        System.out.println(message);
    }

}
