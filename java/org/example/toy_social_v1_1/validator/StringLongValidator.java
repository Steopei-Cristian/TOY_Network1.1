package org.example.toy_social_v1_1.validator;

public class StringLongValidator implements Validator<String> {
    public StringLongValidator() {}

    @Override
    public boolean validate(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid input: " + s);
        }
    }
}
