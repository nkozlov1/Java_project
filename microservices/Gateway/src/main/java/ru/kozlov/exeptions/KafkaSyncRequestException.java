package ru.kozlov.exeptions;

public class KafkaSyncRequestException extends RuntimeException {
    public KafkaSyncRequestException(String message) {
        super(message);
    }
}
