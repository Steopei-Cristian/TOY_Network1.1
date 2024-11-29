package org.example.toy_social_v1_1.validator;

public interface Validator<T> {
    boolean validate(T t);
}
