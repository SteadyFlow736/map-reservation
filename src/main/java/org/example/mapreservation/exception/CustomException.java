package org.example.mapreservation.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private static final String DELIMITER = ": ";

    private final ErrorCode errorCode;
    private final String combinedMessage;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.combinedMessage = errorCode.getMessage();
    }

    public CustomException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.combinedMessage = errorCode.getMessage();
    }

    public CustomException(ErrorCode errorCode, String additionalMessage) {
        super(combineMessages(errorCode.getMessage(), additionalMessage));
        this.errorCode = errorCode;
        this.combinedMessage = combineMessages(errorCode.getMessage(), additionalMessage);
    }

    private static String combineMessages(String msg1, String msg2) {
        return msg1 + DELIMITER + msg2;
    }

}
