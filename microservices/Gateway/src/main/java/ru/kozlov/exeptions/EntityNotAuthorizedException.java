package ru.kozlov.exeptions;

public class EntityNotAuthorizedException extends RuntimeException {
    public EntityNotAuthorizedException(String username) {
        super(username);
    }
}
