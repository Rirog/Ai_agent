package org.example.service;

import jakarta.mail.Message;

import lombok.SneakyThrows;

public class EmailParser {

    @SneakyThrows
    public String parse(Message message) {
        String result;
        String subject = message.getSubject();
        String from = " " + message.getFrom()[0].toString() + " ";
        String text = getText(message);

        result = subject + from + text;

        return result;
    }

    @SneakyThrows
    private String getText(Message message) {

        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        }

        if (message.isMimeType("text/html")) {
            return cleanHtml(message.getContent().toString());
        }
        return "";
    }

    private String cleanHtml(String html) {
        return html
                .replaceAll("<[^>]*>", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}

