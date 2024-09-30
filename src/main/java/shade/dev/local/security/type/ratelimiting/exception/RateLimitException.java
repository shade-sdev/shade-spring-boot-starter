package shade.dev.local.security.type.ratelimiting.exception;

import org.springframework.http.HttpStatus;

import shade.dev.local.exception.WebCoreException;
import shade.dev.local.exception.model.WebErrorCode;

public class RateLimitException extends WebCoreException {

    public RateLimitException() {
        super("You are being rate limited", new WebErrorCode() {

            @Override
            public String getCode() {
                return "RATE_LIMIT_EXCEEDED";
            }

            @Override
            public HttpStatus getStatus() {
                return HttpStatus.TOO_MANY_REQUESTS;
            }

        });
    }

}
