package Domain.Authenticators;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;

// This class is used to validate email addresses for the users.
@Component
public class EmailValidator {
    
    // Regular expression pattern for validating email addresses
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    // Method to validate an email address
    public boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
