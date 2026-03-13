package org.example.cmdStart;

import lombok.SneakyThrows;

import java.io.File;

public class CmdStart {
    private final String path = System.getProperty("user.dir");

    @SneakyThrows
    public void commitProject(String message) {
        ProcessBuilder processBuilder = new ProcessBuilder()
                .directory(new File(path));
        gitAdd();

        processBuilder.command("git", "commit", "-m", message);
        Process process = processBuilder.start();

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("Коммит успешно создан!");
        } else {
            System.out.println("Ошибка при коммите");
        }
    }

    @SneakyThrows
    public void gitAdd() {
        ProcessBuilder processBuilder = new ProcessBuilder()
                .directory(new File(path));

        processBuilder.command("git", "add", ".");
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("Файлы добавлены в индекс");
        } else {
            System.out.println("Ошибка при git add");
        }
    }
}
