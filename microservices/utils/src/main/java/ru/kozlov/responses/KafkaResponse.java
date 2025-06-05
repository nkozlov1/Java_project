package ru.kozlov.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaResponse<T> {
    private T data;
    private String errorMessage;
    private ErrorCode errorCode;
    private boolean success;

    public static <T> KafkaResponse<T> success(T data) {
        KafkaResponse<T> response = new KafkaResponse<>();
        response.setData(data);
        response.setSuccess(true);
        return response;
    }

    public static <T> KafkaResponse<T> error(ErrorCode errorCode, String errorMessage) {
        KafkaResponse<T> response = new KafkaResponse<>();
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        return response;
    }

    public enum ErrorCode {
        NOT_FOUND,
        VALIDATION_ERROR,
        INTERNAL_ERROR
    }
}
