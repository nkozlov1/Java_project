package ru.kozlov.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.kozlov.configs.KafkaSender;
import ru.kozlov.dtos.CatDto;
import ru.kozlov.dtos.CatFilter;
import ru.kozlov.dtos.PageRequestDto;
import ru.kozlov.requests.GetAllCatsRequest;
import ru.kozlov.requests.MakeFriendRequest;
import ru.kozlov.requests.UpdateCatRequest;
import ru.kozlov.responses.CatListResponse;
import ru.kozlov.responses.KafkaResponse;

import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/cats")
@Tag(name = "Cats", description = "Контроллер для операций с котами")
public class CatController {
    private final KafkaSender kafkaSender;

    @Autowired
    public CatController(KafkaSender kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

    @Operation(summary = "Создать нового кота")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createCat(@Valid @RequestBody CatDto catDto) {
        KafkaResponse<CatDto> response = kafkaSender.sendSync(catDto, "save_cat", "cat-service", CatDto.class);
        return handleResponse(response);
    }

    @Operation(summary = "Удалить кота по id")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @CheckPermissionsService.isCatOwner(#id, @CheckPermissionsService.getCurrentUserId())")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        KafkaResponse<CatDto> response = kafkaSender.sendSync(id, "delete_cat_by_id", "cat-service", CatDto.class);
        return handleResponse(response);
    }

    @Operation(summary = "Удалить всех котов")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping
    public void deleteAll() {
        kafkaSender.sendAsync("DELETE_ALL", "delete_all_cat");
    }

    @Operation(summary = "Получить кота по id")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCatById(@PathVariable("id") Long id) {
        KafkaResponse<CatDto> response = kafkaSender.sendSync(id, "get_cat_by_id", "cat-service", CatDto.class);
        return handleResponse(response);
    }

    @Operation(summary = "Список котов с фильтрацией и пагинацией")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllCats(@ModelAttribute CatFilter filter,
                                   @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                   @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        GetAllCatsRequest request = new GetAllCatsRequest(filter, PageRequestDto.of(PageRequest.of(offset, limit)));
        KafkaResponse<CatListResponse> response = kafkaSender.sendSync(request, "get_all_cat", "cat-service", CatListResponse.class);
        return handleResponse(response);
    }

    @Operation(summary = "Получить друзей кота")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/friends")
    public ResponseEntity<?> getAllFriends(@PathVariable("id") Long id) {
        KafkaResponse<CatListResponse> response = kafkaSender.sendSync(id, "get_all_cat_friends", "cat-service", CatListResponse.class);
        return handleResponse(response);
    }

    @Operation(summary = "Получить всех котов владельца")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/all-cats")
    public ResponseEntity<?> getAllCatsById(@PathVariable("id") Long id) {
        KafkaResponse<CatListResponse> response = kafkaSender.sendSync(id, "get_cat_by_owner_id", "cat-service", CatListResponse.class);
        return handleResponse(response);
    }

    @Operation(summary = "Сделать двух котов друзьями")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @CheckPermissionsService.isCatOwner(#catId, @CheckPermissionsService.getCurrentUserId())")
    @PostMapping("/{catId}/friends/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable("catId") long catId, @PathVariable("friendId") Long friendId) {
        MakeFriendRequest request = new MakeFriendRequest(catId, friendId);
        KafkaResponse<CatDto> response = kafkaSender.sendSync(request, "add_cat_friend", "cat-service", CatDto.class);
        return handleResponse(response);
    }

    @Operation(summary = "Обновление кота")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @CheckPermissionsService.isCatOwner(#id, @CheckPermissionsService.getCurrentUserId())")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCat(@PathVariable("id") Long id, @RequestBody CatDto dto) {
        UpdateCatRequest request = new UpdateCatRequest(id, dto);
        KafkaResponse<CatDto> response = kafkaSender.sendSync(request, "update_cat", "cat-service", CatDto.class);
        return handleResponse(response);
    }

    private <T> ResponseEntity<?> handleResponse(KafkaResponse<T> response) {
        if (response.getErrorCode() != null) {
            return switch (response.getErrorCode()) {
                case NOT_FOUND -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(response.getErrorMessage());
                case INTERNAL_ERROR -> ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(response.getErrorMessage());
                default -> ResponseEntity
                        .badRequest()
                        .body(response.getErrorMessage());
            };
        }
        return ResponseEntity.ok(response.getData());
    }
}

