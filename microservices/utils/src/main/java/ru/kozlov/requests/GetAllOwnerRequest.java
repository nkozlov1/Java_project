package ru.kozlov.requests;

import ru.kozlov.dtos.OwnerFilter;
import ru.kozlov.dtos.PageRequestDto;

public record GetAllOwnerRequest(OwnerFilter filter, PageRequestDto pageable) {
}
