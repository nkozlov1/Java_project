package ru.kozlov.services;

import ru.kozlov.requests.*;
import ru.kozlov.dtos.CatOwnerDto;
import ru.kozlov.responses.KafkaResponse;
import ru.kozlov.responses.OwnerListResponse;

import java.util.List;

public interface CatOwnerService {
    KafkaResponse<CatOwnerDto> save(SaveOwnerRequest request);

    KafkaResponse<CatOwnerDto> deleteById(Long id);

    void deleteAll(String command);

    KafkaResponse<CatOwnerDto> update(UpdateOwnerRequest request);

    KafkaResponse<CatOwnerDto> getById(Long id);

    KafkaResponse<OwnerListResponse> getAll(GetAllOwnerRequest ownerRequest);

    KafkaResponse<CatOwnerDto> addCat(AddCatRequest request);

    KafkaResponse<Boolean> isOwner(CheckOwnerRequest request);
}