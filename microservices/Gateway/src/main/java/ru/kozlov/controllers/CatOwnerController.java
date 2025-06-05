package ru.kozlov.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.kozlov.configs.KafkaSender;
import ru.kozlov.dtos.CatOwnerDto;
import ru.kozlov.dtos.OwnerFilter;
import ru.kozlov.dtos.PageRequestDto;
import ru.kozlov.models.UserDto;
import ru.kozlov.requests.AddCatRequest;
import ru.kozlov.requests.GetAllOwnerRequest;
import ru.kozlov.requests.SaveOwnerRequest;
import ru.kozlov.requests.UpdateOwnerRequest;
import ru.kozlov.responses.KafkaResponse;
import ru.kozlov.responses.OwnerListResponse;
import ru.kozlov.services.CheckPermissionsService;
import ru.kozlov.services.UserService;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/owners")
@Tag(name = "Owners", description = "Операции с владельцами")
public class CatOwnerController {
    private final KafkaSender kafkaSender;
    private final CheckPermissionsService checkPermissionsService;

    @Autowired
    public CatOwnerController(KafkaSender kafkaSender, CheckPermissionsService checkPermissionsService) {
        this.kafkaSender = kafkaSender;
        this.checkPermissionsService = checkPermissionsService;
    }

    @Operation(summary = "Создать владельца")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> save(@Valid @RequestBody CatOwnerDto ownerDto) {
        SaveOwnerRequest request = new SaveOwnerRequest(ownerDto, checkPermissionsService.getCurrentUserId());
        KafkaResponse<CatOwnerDto> response = kafkaSender.sendSync(request, "save_cat_owner", "owner-service", CatOwnerDto.class);
        return handleResponse(response);
    }

    @Operation(summary = "Удалить владельца по id")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @CheckPermissionsService.isOwner(#id, @CheckPermissionsService.getCurrentUserId())")
    public ResponseEntity<?> deleteOwnerById(@PathVariable("id") Long id) {
        KafkaResponse<CatOwnerDto> response = kafkaSender.sendSync(id, "delete_cat_owner_by_id", "owner-service", CatOwnerDto.class);
        return handleResponse(response);
    }

    @Operation(summary = "Удалить всех владельцев")
    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteAll() {
        kafkaSender.sendAsync("DELETE_ALL", "delete_all_cat_owners");
    }

    @Operation(summary = "Получить владельца по id")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getOwnerById(@PathVariable("id") Long id) {
        KafkaResponse<CatOwnerDto> response = kafkaSender.sendSync(id, "get_cat_owner_by_id", "owner-service", CatOwnerDto.class);
        return handleResponse(response);
    }

    @Operation(summary = "Список владельцев c фильтром и пагинацией")
    @GetMapping("/getAll")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllOwners(@ModelAttribute OwnerFilter filter,
                                          @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                          @RequestParam(value = "limit", defaultValue = "20") Integer limit) {
        GetAllOwnerRequest request = new GetAllOwnerRequest(filter, PageRequestDto.of(PageRequest.of(offset, limit)));
        KafkaResponse<OwnerListResponse> response = kafkaSender.sendSync(request, "get_all_cat_owners", "owner-service", OwnerListResponse.class);
        return ResponseEntity.ok(handleResponse(response));
    }

    @Operation(summary = "Добавить кота владельцу")
    @PostMapping("/{ownerId}/cats/{catId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @CheckPermissionsService.isOwner(#ownerId, @CheckPermissionsService.getCurrentUserId())")
    public ResponseEntity<?> addCat(@PathVariable("ownerId") Long ownerId, @PathVariable("catId") Long catId) {
        AddCatRequest request = new AddCatRequest(ownerId, catId);
        KafkaResponse<CatOwnerDto> response = kafkaSender.sendSync(request, "add_owner_to_cat", "owner-service", CatOwnerDto.class);
        return handleResponse(response);
    }

    @Operation(summary = "Обновление владельца")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @CheckPermissionsService.isOwner(#id, @CheckPermissionsService.getCurrentUserId())")
    public ResponseEntity<?> updateOwner(@PathVariable("id") Long id, @RequestBody CatOwnerDto dto) {
        UpdateOwnerRequest request = new UpdateOwnerRequest(id, dto);
        KafkaResponse<CatOwnerDto> response = kafkaSender.sendSync(request, "update_cat_owner", "owner-service", CatOwnerDto.class);
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

