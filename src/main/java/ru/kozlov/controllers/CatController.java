package ru.kozlov.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.kozlov.models.CatDto;
import ru.kozlov.models.CatFilter;
import ru.kozlov.services.CatService;

import java.util.List;

@RestController
@RequestMapping("/cats")
@Tag(name = "Cats", description = "Контроллер для операций с котами")
public class CatController {
    private final CatService service;

    @Autowired
    public CatController(CatService service) {
        this.service = service;
    }

    @Operation(summary = "Создать нового кота")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public CatDto createCat(@Valid @RequestBody CatDto catDto) {
        return service.save(catDto);
    }

    @Operation(summary = "Удалить кота по id")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @catService.isCatOwner(#id, authentication.principal.username)")
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        service.deleteById(id);
    }

    @Operation(summary = "Удалить всех котов")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping
    public void deleteAll() {
        service.deleteAll();
    }

    @Operation(summary = "Получить кота по id")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public CatDto getCatById(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Список котов с фильтрацией и пагинацией")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getAll")
    public List<CatDto> getAllCats(@ModelAttribute CatFilter filter,
                                   @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                   @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        return service.getAll(filter, PageRequest.of(offset, limit));
    }

    @Operation(summary = "Получить друзей кота")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/friends")
    public List<CatDto> getAllFriends(@PathVariable("id") Long id) {
        return service.getAllFriends(id);
    }

    @Operation(summary = "Получить всех котов владельца")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/all-cats")
    public List<CatDto> getAllCatsById(@PathVariable("id") Long id) {
        return service.getAllCatsById(id);
    }

    @Operation(summary = "Сделать двух котов друзьями")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @catService.isCatOwner(#catId, authentication.principal.username)")
    @PostMapping("/{catId}/friends/{friendId}")
    public void addFriend(@PathVariable("catId") long catId, @PathVariable("friendId") Long friendId) {
        service.addFriend(catId, friendId);
    }

    @Operation(summary = "Обновление кота")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @catService.isCatOwner(#id, authentication.principal.username)")
    @PutMapping("/{id}")
    public CatDto updateCat(@PathVariable("id") Long id, @RequestBody CatDto dto) {
        return service.update(id, dto);
    }
}

