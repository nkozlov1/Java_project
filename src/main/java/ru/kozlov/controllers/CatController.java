package ru.kozlov.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import ru.kozlov.models.CatDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.kozlov.models.CatFilter;
import ru.kozlov.services.CatService;

import java.util.List;

@RestController
@RequestMapping("/cats")
@Component
public class CatController {
    private final CatService service;

    @Autowired
    public CatController(CatService service) {
        this.service = service;
    }

    @PostMapping
    public CatDto createCat(@Valid @RequestBody CatDto catDto) {
        return service.save(catDto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        service.deleteById(id);
    }

    @DeleteMapping
    public void deleteAll() {
        service.deleteAll();
    }

    @GetMapping("/{id}")
    public CatDto getCatById(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @GetMapping("/getAll")
    public List<CatDto> getAllCats(@ModelAttribute CatFilter filter,
                                   @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                   @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        return service.getAll(filter, PageRequest.of(offset, limit));
    }

    @GetMapping("/{id}/friends")
    public List<CatDto> getAllFriends(@PathVariable("id") Long id) {
        return service.getAllFriends(id);
    }

    @GetMapping("/{id}/all-cats")
    public List<CatDto> getAllCatsById(@PathVariable("id") Long id) {
        return service.getAllCatsById(id);
    }

    @PostMapping("/{catId}/friends/{friendId}")
    public void addFriend(@PathVariable("catId") long catId, @PathVariable("friendId") Long friendId) {
        service.addFriend(catId, friendId);
    }

    @PutMapping("/{id}")
    public CatDto updateCat(@PathVariable("id") Long id, @RequestBody CatDto dto) {
        return service.update(id, dto);
    }
}

