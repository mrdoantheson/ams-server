package ams.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class TokenExpiredException extends NullPointerException {

    public TokenExpiredException(String message) {
        super(message);
    }
}
