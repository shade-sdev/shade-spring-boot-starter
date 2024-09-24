package shade.dev.local.security;

import org.springframework.http.HttpStatus;

public class RateLimitException extends RuntimeException {

    public RateLimitException()
    {
        super("You are being rate limited");
        String code = "RATE_LIMIT_EXCEEDED";
        HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;
    }
}
