package ru.kozlov.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.kozlov.exceptions.CatNotFoundException;
import ru.kozlov.mappers.ColorsDtoMapper;
import ru.kozlov.models.CatDto;
import ru.kozlov.models.Cat;
import ru.kozlov.mappers.CatDtoMapper;
import ru.kozlov.models.CatFilter;
import ru.kozlov.repositories.CatRepository;

import java.util.List;
import java.util.Set;

@Service
public class CatServiceImpl implements CatService {
    private final CatRepository catRepository;

    @Autowired
    public CatServiceImpl(CatRepository catRepository) {
        this.catRepository = catRepository;
    }


    public CatDto save(CatDto catDto) {
        Cat cat = CatDtoMapper.toDao(catDto);
        Cat resultCat = catRepository.save(cat);
        return CatDtoMapper.toDto(resultCat);
    }

    public void deleteById(Long id) {
        Cat cat = catRepository.findById(id)
                .orElseThrow(() -> new CatNotFoundException(id));

        for (Cat friend : cat.getFriends()) {
            friend.getFriends().remove(cat);
        }
        cat.getFriends().clear();
        catRepository.saveAll(cat.getFriends());
        catRepository.delete(cat);
    }

    public void deleteAll() {
        catRepository.deleteAll();
    }

    public CatDto getById(Long id) {
        return catRepository.findById(id).map(CatDtoMapper::toDto).orElseThrow(
                () -> new CatNotFoundException(id)
        );
    }

    public List<CatDto> getAll(CatFilter filter, Pageable pageable) {
        Page<Cat> cats = catRepository.findAll(filter.toSpecification(), pageable);
        return cats.stream()
                .map(CatDtoMapper::toDto)
                .toList();
    }

    public List<CatDto> getAllFriends(Long id) {
        Cat cat = catRepository.findById(id)
                .orElseThrow(() -> new CatNotFoundException(id));
        Set<Cat> friends = cat.getFriends();
        return friends.stream()
                .map(CatDtoMapper::toDto)
                .toList();
    }

    public List<CatDto> getAllCatsById(Long id) {
        List<Cat> cats = catRepository.findCatsByOwnerId(id);
        return cats.stream()
                .map(CatDtoMapper::toDto)
                .toList();
    }

    public void addFriend(Long catId, Long friendId) {
        Cat cat = catRepository.findById(catId)
                .orElseThrow(() -> new CatNotFoundException(catId));
        Cat friend = catRepository.findById(friendId)
                .orElseThrow(() -> new CatNotFoundException(friendId));
        cat.addFriend(friend);
        friend.addFriend(cat);
        catRepository.save(cat);
        catRepository.save(friend);
    }

    public CatDto update(Long id, CatDto catDto) {
        Cat cat = catRepository.findById(id)
                .orElseThrow(() -> new CatNotFoundException(id));
        cat.setName(catDto.getName());
        cat.setColor(ColorsDtoMapper.toDao(catDto.getColor()));
        cat.setBirthDate(catDto.getBirthDate());
        cat.setBreed(catDto.getBreed());
        Cat updated = catRepository.save(cat);
        return CatDtoMapper.toDto(updated);
    }
}
