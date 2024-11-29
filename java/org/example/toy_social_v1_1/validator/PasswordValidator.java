package org.example.toy_social_v1_1.validator;

public class PasswordValidator implements Validator<String> {
    public PasswordValidator() {}

    @Override
    public boolean validate(String password) {
        if(!(password.length() >= 8))
            throw new RuntimeException("Password should be at least 8 characters");
        int capsCount = 0, lowsCount = 0;
        for (int i = 0; i < password.length(); i++) {
            if(Character.isUpperCase(password.charAt(i)))
                capsCount++;
            else if(Character.isLowerCase(password.charAt(i)))
                lowsCount++;
        }
        if(capsCount == 0)
            throw new RuntimeException("Password must contain an uppercase");
        if(lowsCount == 0)
            throw new RuntimeException("Password must contain a lowercase");
        return true;
    }
}
