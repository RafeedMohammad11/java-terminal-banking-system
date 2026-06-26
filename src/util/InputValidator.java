package util;

public class InputValidator {

    // Standard email regex — checks for user@domain.ext format
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";

    // Exactly 11 digits, no spaces or dashes
    private static final String PHONE_REGEX = "^\\d{11}$";

    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return email.matches(EMAIL_REGEX);
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isBlank()) return false;
        return phone.matches(PHONE_REGEX);
    }

    // Returns error message string, or null if valid
    public static String validateEmail(String email) {
        if (email == null || email.isBlank()) {
            return "Email is required.";
        }
        if (!email.matches(EMAIL_REGEX)) {
            return "Invalid email format. Example: name@domain.com";
        }
        return null;
    }

    public static String validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return "Phone number is required.";
        }
        if (!phone.matches(PHONE_REGEX)) {
            return "Phone must be exactly 11 digits. Example: 01711000000";
        }
        return null;
    }

    public static String validateAmount(String input) {
        if (input == null || input.isBlank()) {
            return "Amount is required.";
        }
        try {
            double val = Double.parseDouble(input);
            if (val <= 0) return "Amount must be greater than zero.";
        } catch (NumberFormatException e) {
            return "Amount must be a valid number.";
        }
        return null;
    }

    public static String validateAccountName(String name) {
        if (name == null || name.isBlank()) {
            return "Holder name is required.";
        }
        if (!name.matches("^[A-Z\\s]{2,50}$")) {
            return "Name must be 2-50 characters long and contain only uppercase letters and spaces.";
        }
        return null;
    }
}