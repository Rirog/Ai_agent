package org.example.service;

import jakarta.mail.BodyPart;
import jakarta.mail.Message;

import jakarta.mail.Multipart;
import jakarta.mail.Part;
import lombok.SneakyThrows;

public class EmailParser {

    @SneakyThrows
    public String parse(Message message) {
        String result;
        String subject = message.getSubject() + " ";
        String from = message.getFrom()[0].toString() + " ";
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

        if (message.isMimeType("multipart/*")) {
            return getTextFromMultipart((Multipart) message.getContent());
        }

        return "";
    }

    @SneakyThrows
    private String getTextFromMultipart(Multipart multipart) {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < multipart.getCount(); i++) {

            BodyPart part = multipart.getBodyPart(i);

            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                continue;
            }

            if (part.isMimeType("text/plain")) {
                result.append(part.getContent().toString()).append("\n");
            }

            else if (part.isMimeType("text/html")) {
                result.append(cleanHtml(part.getContent().toString())).append("\n");
            }

            else if (part.getContent() instanceof Multipart) {
                result.append(getTextFromMultipart((Multipart) part.getContent()));
            }
        }

        return result.toString();
    }

    private String cleanHtml(String html) {
        return html
                .replaceAll("<[^>]*>", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}

