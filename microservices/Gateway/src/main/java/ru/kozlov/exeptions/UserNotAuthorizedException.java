package ru.kozlov.exeptions;

public class UserNotAuthorizedException extends EntityNotAuthorizedException {
    public UserNotAuthorizedException(String username) {
        super("User:" + username + " is not authorized");
    }
}
