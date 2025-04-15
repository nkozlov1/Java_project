package ru.kozlov.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.kozlov.exceptions.CatNotFoundException;
import ru.kozlov.exceptions.CatOwnerNotFoundException;
import ru.kozlov.mappers.CatOwnerDtoMapper;
import ru.kozlov.models.Cat;
import ru.kozlov.models.CatOwner;
import ru.kozlov.models.CatOwnerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kozlov.models.OwnerFilter;
import ru.kozlov.repositories.CatOwnerRepository;
import ru.kozlov.repositories.CatRepository;

import java.util.List;

@Service
public class CatOwnerServiceImpl implements CatOwnerService {

    private final CatRepository catRepository;
    private final CatOwnerRepository catOwnerRepository;


    @Autowired
    public CatOwnerServiceImpl(CatRepository catRepository, CatOwnerRepository catOwnerRepository) {
        this.catRepository = catRepository;
        this.catOwnerRepository = catOwnerRepository;
    }

    public CatOwnerDto save(CatOwnerDto ownerDto) {
        CatOwner owner = CatOwnerDtoMapper.toDao(ownerDto);
        CatOwner resultOwner = catOwnerRepository.save(owner);
        return CatOwnerDtoMapper.toDto(resultOwner);
    }

    public void deleteById(Long id) {
        CatOwner owner = catOwnerRepository.findById(id)
                .orElseThrow(() -> new CatOwnerNotFoundException(id));

        for (Cat cat : owner.getPets()) {
            cat.setOwner(null);
        }

        catRepository.saveAll(owner.getPets());
        owner.getPets().clear();
        catOwnerRepository.delete(owner);
    }


    public void deleteAll() {
        catOwnerRepository.deleteAll();
    }


    public CatOwnerDto getById(Long id) {
        return catOwnerRepository.findById(id).map(CatOwnerDtoMapper::toDto).orElseThrow(
                () -> new CatOwnerNotFoundException(id)
        );
    }

    public List<CatOwnerDto> getAll(OwnerFilter filter, Pageable pageable) {
        Page<CatOwner> owners = catOwnerRepository.findAll(filter.toSpecification(), pageable);
        return owners.stream()
                .map(CatOwnerDtoMapper::toDto)
                .toList();
    }


    public void addCat(Long id, Long catId) {
        CatOwner owner = catOwnerRepository.findById(id)
                .orElseThrow(() -> new CatOwnerNotFoundException(id));
        Cat cat = catRepository.findById(catId)
                .orElseThrow(() -> new CatNotFoundException(catId));

        owner.getPets().add(cat);
        cat.setOwner(owner);
        catOwnerRepository.save(owner);
        catRepository.save(cat);
    }

    public CatOwnerDto update(Long id, CatOwnerDto ownerDto) {
        CatOwner owner = catOwnerRepository.findById(id)
                .orElseThrow(() -> new CatOwnerNotFoundException(id));
        owner.setName(ownerDto.getName());
        owner.setBirthDate(ownerDto.getBirthDate());
        CatOwner updated = catOwnerRepository.save(owner);
        return CatOwnerDtoMapper.toDto(updated);
    }
}
