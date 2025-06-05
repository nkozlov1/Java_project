package ru.kozlov.services;


import ru.kozlov.requests.*;
import ru.kozlov.dtos.CatDto;
import ru.kozlov.responses.CatListResponse;
import ru.kozlov.responses.KafkaResponse;
import ru.kozlov.responses.OwnerListResponse;

import java.util.List;

public interface CatService {
    KafkaResponse<CatDto> save(CatDto catDto);

    KafkaResponse<CatDto> deleteById(Long id);

    void deleteAll(String command);

    KafkaResponse<CatDto> getById(Long id);

    KafkaResponse<CatDto> update(UpdateCatRequest request);

    KafkaResponse<CatListResponse> getAll(GetAllCatsRequest request);

    KafkaResponse<CatListResponse> getAllFriends(Long id);

    KafkaResponse<CatListResponse> getAllCatsById(Long id);

    KafkaResponse<CatDto> addFriend(MakeFriendRequest request);

    KafkaResponse<CatDto> addOwner(AddCatRequest request);

    KafkaResponse<Boolean> isCatOwner(CheckCatOwnerRequest request);
}

