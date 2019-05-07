package com.autohome.mobile.druidservice.exception;

public class AnnotationException extends Exception {
    public AnnotationException() {
    }

    public AnnotationException(String message) {
        super(message);
    }

    public AnnotationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnnotationException(Throwable cause) {
        super(cause);
    }

    public AnnotationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
