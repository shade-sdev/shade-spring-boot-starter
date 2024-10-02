package shade.dev.local.exception;

import java.util.UUID;

import org.springframework.http.HttpStatusCode;

import shade.dev.local.exception.model.WebErrorCode;

public class WebCoreException extends RuntimeException {

    private final String code;

    private final HttpStatusCode statusCode;

    public WebCoreException(String message, WebErrorCode errorCode) {
        super(message);
        this.code = errorCode.getCode();
        this.statusCode = errorCode.getStatus();
    }

    public WebCoreException(String message, WebErrorCode errorCode, Class<?> clazz) {
        super(String.format("%s %s", clazz.getSimpleName(), message));
        this.code = String.format("%s_%s", clazz.getSimpleName().toUpperCase(), errorCode.getCode());
        this.statusCode = errorCode.getStatus();
    }

    public WebCoreException(String message, WebErrorCode errorCode, UUID id, Class<?> clazz) {
        super(String.format("%s (id = %s) %s", clazz.getSimpleName(), id, message));
        this.code = String.format("%s_%s", clazz.getSimpleName().toUpperCase(), errorCode.getCode());
        this.statusCode = errorCode.getStatus();
    }

    public WebCoreException(String message, WebErrorCode errorCode, String idType, UUID id, Class<?> clazz) {
        super(String.format("%s from (%s = %s) %s", clazz.getSimpleName(), idType, id, message));
        this.code = String.format("%s_%s", clazz.getSimpleName().toUpperCase(), errorCode.getCode());
        this.statusCode = errorCode.getStatus();
    }

    public String getCode() {
        return this.code;
    }

    public HttpStatusCode getStatusCode() {
        return this.statusCode;
    }

}
