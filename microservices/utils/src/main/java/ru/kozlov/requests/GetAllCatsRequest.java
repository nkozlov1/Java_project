package ru.kozlov.requests;

import ru.kozlov.dtos.CatFilter;
import ru.kozlov.dtos.PageRequestDto;

public record GetAllCatsRequest(CatFilter filter, PageRequestDto pageable) {
}
