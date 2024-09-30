package shade.dev.local.exception.model;

import org.springframework.http.HttpStatus;

public interface WebErrorCode {

    String getCode();
    HttpStatus getStatus();

}
