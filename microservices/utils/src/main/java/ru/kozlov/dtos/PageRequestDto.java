package ru.kozlov.dtos;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record PageRequestDto(int page, int size) {

    public static PageRequestDto of(Pageable pageable) {
        return new PageRequestDto(pageable.getPageNumber(), pageable.getPageSize());
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size);
    }
}