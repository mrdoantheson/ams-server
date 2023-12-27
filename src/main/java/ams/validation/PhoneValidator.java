package ams.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneValidator {

    private static final String PHONE_REGEX = "^(\\+84|0)\\d{9}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

    public boolean isValidPhone(String email) {
        Matcher matcher = PHONE_PATTERN.matcher(email);
        return matcher.matches();
    }
}
