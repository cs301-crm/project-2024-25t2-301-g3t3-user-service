package com.cs301.crm.utils;

import java.security.SecureRandom;

public class PasswordUtil {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARS;
    private static final int length = 9;
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generatePassword() {

        StringBuilder password = new StringBuilder(length);

        // Ensure at least one character from each category
        password.append(getRandomChar(UPPERCASE));
        password.append(getRandomChar(LOWERCASE));
        password.append(getRandomChar(DIGITS));
        password.append(getRandomChar(SPECIAL_CHARS));

        // Fill the rest of the password with random characters
        for (int i = 4; i < length; i++) {
            password.append(getRandomChar(ALL_CHARS));
        }

        // Shuffle the password to avoid predictable order
        return shuffleString(password.toString());
    }

    public static int generateOtp() {
        return secureRandom.nextInt(900000) + 100000;
    }

    private static char getRandomChar(String characters) {
        return characters.charAt(secureRandom.nextInt(characters.length()));
    }

    private static String shuffleString(String input) {
        char[] array = input.toCharArray();
        for (int i = array.length - 1; i > 0; i--) {
            int index = secureRandom.nextInt(i + 1);
            char temp = array[i];
            array[i] = array[index];
            array[index] = temp;
        }
        return new String(array);
    }

}
