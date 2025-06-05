package ru.kozlov.requests;

import ru.kozlov.dtos.CatOwnerDto;

public record UpdateOwnerRequest(Long id, CatOwnerDto dto) {
}
