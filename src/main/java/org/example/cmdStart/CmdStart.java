package org.example.cmdStart;

import lombok.SneakyThrows;

import java.io.IOException;

public class CmdStart {
    private final Runtime runtime = Runtime.getRuntime();

    @SneakyThrows
    public void commitProject(String message) {
        gitAdd();

        String command = "git commit -m \"" + message + "\"";
        Process process = runtime.exec(command);

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("Коммит успешно создан!");
        } else {
            System.out.println("Ошибка при коммите");
        }
    }

    @SneakyThrows
    public void gitAdd() {
        String command = "git add .";

        Process process = runtime.exec(command);
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("Файлы добавлены в индекс");
        } else {
            System.out.println("Ошибка при git add");
        }
    }
}
