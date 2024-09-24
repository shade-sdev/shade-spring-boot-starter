package shade.dev.local.exception;

import shade.dev.local.exception.model.WebErrorCode;

import java.util.UUID;

public class WebCoreException extends RuntimeException {

    private final String code;

    private final WebErrorCode errorCode;

    public WebCoreException(String message, WebErrorCode errorCode)
    {
        super(message);
        this.errorCode = errorCode;
        this.code = errorCode.getCode();
    }

    public WebCoreException(String message, WebErrorCode errorCode, Class<?> clazz)
    {
        super(String.format("%s %s", clazz.getSimpleName(), message));
        this.errorCode = errorCode;
        this.code = String.format("%s_%s", clazz.getSimpleName().toUpperCase(), errorCode.getCode());
    }

    public WebCoreException(String message, WebErrorCode errorCode, UUID id, Class<?> clazz)
    {
        super(String.format("%s (id = %s) %s", clazz.getSimpleName(), id, message));
        this.errorCode = errorCode;
        this.code = String.format("%s_%s", clazz.getSimpleName().toUpperCase(), errorCode.getCode());
    }

    public WebCoreException(String message, WebErrorCode errorCode, String idType, UUID id, Class<?> clazz)
    {
        super(String.format("%s from (%s = %s) %s", clazz.getSimpleName(), idType, id, message));
        this.errorCode = errorCode;
        this.code = String.format("%s_%s", clazz.getSimpleName().toUpperCase(), errorCode.getCode());
    }

    public WebErrorCode getErrorCode()
    {
        return this.errorCode;
    }

    public String getCode()
    {
        return this.code;
    }

}
