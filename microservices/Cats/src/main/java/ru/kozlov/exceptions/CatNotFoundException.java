package ru.kozlov.exceptions;

public class CatNotFoundException extends EntityNotFoundException{
    public CatNotFoundException(Long id) {
        super("Cat with id: " + id + " not found");
    }
}
