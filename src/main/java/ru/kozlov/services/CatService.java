package ru.kozlov.services;


import org.springframework.data.domain.Pageable;
import ru.kozlov.models.CatDto;
import ru.kozlov.models.CatFilter;

import java.util.List;

public interface CatService {
    CatDto save(CatDto catDto);

    void deleteById(Long id);

    void deleteAll();

    CatDto getById(Long id);

    CatDto update(Long id, CatDto catDto);

    List<CatDto> getAll(CatFilter filter, Pageable pageable);

    List<CatDto> getAllFriends(Long id);

    List<CatDto> getAllCatsById(Long id);

    void addFriend(Long catId, Long friendId);

    boolean isCatOwner(Long catId, String username);
}

