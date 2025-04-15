package ru.kozlov.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import ru.kozlov.models.CatOwnerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.kozlov.models.OwnerFilter;
import ru.kozlov.services.CatOwnerService;

import java.util.List;

@RestController
@RequestMapping("/owners")
@Component
public class CatOwnerController {
    private final CatOwnerService service;

    @Autowired
    public CatOwnerController(CatOwnerService service) {
        this.service = service;
    }

    @PostMapping
    public CatOwnerDto save(@Valid @RequestBody CatOwnerDto ownerDto) {
        return service.save(ownerDto);
    }

    @DeleteMapping("/{id}")
    public void deleteOwnerById(@PathVariable("id") Long id) {
        service.deleteById(id);
    }


    @DeleteMapping
    public void deleteAll() {
        service.deleteAll();
    }

    @GetMapping("/{id}")
    public CatOwnerDto getOwnerById(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @GetMapping("/getAll")
    public List<CatOwnerDto> getAllOwners(@ModelAttribute OwnerFilter filter,
                                          @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                          @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        return service.getAll(filter, PageRequest.of(offset, limit));
    }

    @PostMapping("/{ownerId}/cats/{catId}")
    public void addCat(@PathVariable("ownerId") Long ownerId, @PathVariable("catId") Long catId) {
        service.addCat(ownerId, catId);
    }

    @PutMapping("/{id}")
    public CatOwnerDto updateOwner(@PathVariable("id") Long id, @RequestBody CatOwnerDto dto) {
        return service.update(id, dto);
    }
}
