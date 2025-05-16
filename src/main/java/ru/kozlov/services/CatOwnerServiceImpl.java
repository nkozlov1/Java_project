package ru.kozlov.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.kozlov.exceptions.CatNotFoundException;
import ru.kozlov.exceptions.CatOwnerNotFoundException;
import ru.kozlov.exceptions.UserNotFoundException;
import ru.kozlov.mappers.CatOwnerDtoMapper;
import ru.kozlov.models.*;
import ru.kozlov.repositories.CatOwnerRepository;
import ru.kozlov.repositories.CatRepository;
import ru.kozlov.repositories.UserRepository;

import java.util.List;

@Service("catOwnerService")
public class CatOwnerServiceImpl implements CatOwnerService {

    private final CatRepository catRepository;
    private final CatOwnerRepository catOwnerRepository;
    private final UserRepository userRepository;


    @Autowired
    public CatOwnerServiceImpl(CatRepository catRepository, CatOwnerRepository catOwnerRepository, UserRepository userRepository) {
        this.catRepository = catRepository;
        this.catOwnerRepository = catOwnerRepository;
        this.userRepository = userRepository;
    }

    public CatOwnerDto save(CatOwnerDto ownerDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        CatOwner owner = CatOwnerDtoMapper.toDao(ownerDto);
        owner.setUser(user);
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
        if (ownerDto.getName() != null)
            owner.setName(ownerDto.getName());
        if (ownerDto.getBirthDate() != null)
            owner.setBirthDate(ownerDto.getBirthDate());
        CatOwner updated = catOwnerRepository.save(owner);
        return CatOwnerDtoMapper.toDto(updated);
    }

    public boolean isOwner(Long ownerId, String username) {
        System.out.println(ownerId);
        System.out.println(username);
        CatOwner owner = catOwnerRepository.findById(ownerId).orElse(null);
        if (owner == null) {
            return false;
        }
        User user = owner.getUser();
        return user != null && user.getUsername().equals(username);
    }
}
