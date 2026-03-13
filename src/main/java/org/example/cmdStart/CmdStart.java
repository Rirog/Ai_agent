package org.example.cmdStart;

import lombok.SneakyThrows;

import java.io.File;


public class CmdStart {
    private final Runtime runtime = Runtime.getRuntime();
    private final String path = System.getProperty("user.dir");
    private final ProcessBuilder processBuilder = new ProcessBuilder()
            .directory(new File(path));

    @SneakyThrows
    public void commitProject(String message) {
        gitAdd();

//        String command = "git commit -m \"" + message + "\"";

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
