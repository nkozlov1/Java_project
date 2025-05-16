package ru.kozlov.exceptions;

public class UserNotAuthorizedException extends EntityNotAuthorizedException {
    public UserNotAuthorizedException(String username) {
        super("User:" + username + " is not authorized");
    }
}
