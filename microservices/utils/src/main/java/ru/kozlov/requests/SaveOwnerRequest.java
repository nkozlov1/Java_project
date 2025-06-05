package ru.kozlov.requests;

import ru.kozlov.dtos.CatOwnerDto;

public record SaveOwnerRequest(CatOwnerDto owner, Long userId) {
}
