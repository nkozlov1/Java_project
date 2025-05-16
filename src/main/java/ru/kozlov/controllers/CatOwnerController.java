package ru.kozlov.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.kozlov.models.CatOwnerDto;
import ru.kozlov.models.OwnerFilter;
import ru.kozlov.services.CatOwnerService;

import java.util.List;

@RestController
@RequestMapping("/owners")
@Tag(name = "Owners", description = "Операции с владельцами")
public class CatOwnerController {
    private final CatOwnerService service;

    @Autowired
    public CatOwnerController(CatOwnerService service) {
        this.service = service;
    }

    @Operation(summary = "Создать владельца")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public CatOwnerDto save(@Valid @RequestBody CatOwnerDto ownerDto) {
        return service.save(ownerDto);
    }

    @Operation(summary = "Удалить владельца по id")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @catOwnerService.isOwner(#id, authentication.principal.username)")
    public void deleteOwnerById(@PathVariable("id") Long id) {
        service.deleteById(id);
    }

    @Operation(summary = "Удалить всех владельцев")
    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteAll() {
        service.deleteAll();
    }

    @Operation(summary = "Получить владельца по id")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public CatOwnerDto getOwnerById(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Список владельцев c фильтром и пагинацией")
    @GetMapping("/getAll")
    @PreAuthorize("isAuthenticated()")
    public List<CatOwnerDto> getAllOwners(@ModelAttribute OwnerFilter filter,
                                          @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                          @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        return service.getAll(filter, PageRequest.of(offset, limit));
    }

    @Operation(summary = "Добавить кота владельцу")
    @PostMapping("/{ownerId}/cats/{catId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @catOwnerService.isOwner(#ownerId, authentication.principal.username)")
    public void addCat(@PathVariable("ownerId") Long ownerId, @PathVariable("catId") Long catId) {
        service.addCat(ownerId, catId);
    }

    @Operation(summary = "Обновление владельца")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @catOwnerService.isOwner(#id, authentication.principal.username)")
    public CatOwnerDto updateOwner(@PathVariable("id") Long id, @RequestBody CatOwnerDto dto) {
        return service.update(id, dto);
    }
}

