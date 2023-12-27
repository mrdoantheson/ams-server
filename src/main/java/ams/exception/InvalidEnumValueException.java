package ams.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class InvalidEnumValueException extends RuntimeException {


    public InvalidEnumValueException(String message) {
        super(message);
    }
}
