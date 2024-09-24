package shade.dev.local.security.type.ratelimiting.exception;

import org.springframework.http.HttpStatus;

public class RateLimitException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    public RateLimitException() {
        super("You are being rate limited");
        this.code = "RATE_LIMIT_EXCEEDED";
        this.status = HttpStatus.TOO_MANY_REQUESTS;
    }

    public String getCode() {
        return this.code;
    }

    public HttpStatus getHttpStatus() {
        return this.status;
    }

}
