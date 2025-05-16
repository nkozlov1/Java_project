package ru.kozlov.exceptions;

public class EntityNotAuthorizedException extends RuntimeException {
    public EntityNotAuthorizedException(String username) {
        super(username);
    }
}
