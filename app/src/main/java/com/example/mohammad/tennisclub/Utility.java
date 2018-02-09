package com.example.mohammad.tennisclub;

import java.util.regex.Pattern;

public final class Utility {

    private Utility() {}

    public static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        final String pattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9 ]{8,}$";
        return Pattern.compile(pattern).matcher(password).matches();
    }

    public static boolean isValidName(String name) {
        final String pattern = "^[A-Za-z]+[A-Za-z -]*[A-Za-z]*$";
        return Pattern.compile(pattern).matcher(name).matches();
    }

    public static boolean isValidPhone(String phone) {
        String pattern = "^(\\+44(0)?|0)7\\d{9}$";
        return Pattern.compile(pattern).matcher(phone).matches();
    }
}
