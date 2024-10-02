package shade.dev.local.exception;

import java.util.UUID;

import org.springframework.http.HttpStatusCode;

import shade.dev.local.exception.model.WebErrorCode;

public abstract class WebCoreException extends RuntimeException {

    private final String code;

    private final HttpStatusCode statusCode;

    protected abstract WebErrorCode getErrorCode();

    protected WebCoreException(String message) {
        super(message);
        this.code = this.getErrorCode().getCode();
        this.statusCode = this.getErrorCode().getStatus();
    }

    protected WebCoreException(String message, Class<?> clazz) {
        super(String.format("%s %s", clazz.getSimpleName(), message));
        this.code = String.format("%s_%s", clazz.getSimpleName().toUpperCase(), this.getErrorCode().getCode());
        this.statusCode = this.getErrorCode().getStatus();
    }

    protected WebCoreException(String message, UUID id, Class<?> clazz) {
        super(String.format("%s (id = %s) %s", clazz.getSimpleName(), id, message));
        this.code = String.format("%s_%s", clazz.getSimpleName().toUpperCase(), this.getErrorCode().getCode());
        this.statusCode = this.getErrorCode().getStatus();
    }

    protected WebCoreException(String message, String idType, UUID id, Class<?> clazz) {
        super(String.format("%s from (%s = %s) %s", clazz.getSimpleName(), idType, id, message));
        this.code = String.format("%s_%s", clazz.getSimpleName().toUpperCase(), this.getErrorCode().getCode());
        this.statusCode = this.getErrorCode().getStatus();
    }

    public String getCode() {
        return this.code;
    }

    public HttpStatusCode getStatusCode() {
        return this.statusCode;
    }

}
