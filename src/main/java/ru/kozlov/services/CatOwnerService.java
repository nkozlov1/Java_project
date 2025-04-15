package ru.kozlov.services;

import org.springframework.data.domain.Pageable;
import ru.kozlov.models.CatOwner;
import ru.kozlov.models.CatOwnerDto;
import ru.kozlov.models.OwnerFilter;

import java.util.List;

public interface CatOwnerService {
    CatOwnerDto save(CatOwnerDto ownerDto);

    void deleteById(Long id);

    void deleteAll();

    CatOwnerDto update(Long id, CatOwnerDto ownerDto);

    CatOwnerDto getById(Long id);

    List<CatOwnerDto> getAll(OwnerFilter filter, Pageable pageable);

    void addCat(Long id, Long catId);
}
