package ru.kozlov.exceptions;

public class CatOwnerNotFoundException extends EntityNotFoundException {
    public CatOwnerNotFoundException(Long id) {
        super("Person with id: " + id + " not found");
    }
}
