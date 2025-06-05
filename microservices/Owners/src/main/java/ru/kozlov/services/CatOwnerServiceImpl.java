package ru.kozlov.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import ru.kozlov.exceptions.CatOwnerNotFoundException;
import ru.kozlov.requests.*;
import ru.kozlov.dtos.CatOwnerDto;
import ru.kozlov.models.CatOwner;
import ru.kozlov.models.CatOwnerDtoMapper;
import ru.kozlov.models.CatOwnerSpecifications;
import ru.kozlov.repositories.CatOwnerRepository;
import ru.kozlov.responses.KafkaResponse;
import ru.kozlov.responses.OwnerListResponse;

import java.util.List;

@Service("catOwnerService")
public class CatOwnerServiceImpl implements CatOwnerService {
    CatOwnerRepository catOwnerRepository;

    @Autowired
    public CatOwnerServiceImpl(CatOwnerRepository catOwnerRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.catOwnerRepository = catOwnerRepository;
    }


    @KafkaListener(topics = "save_cat_owner", groupId = "cat-owner-group")
    @SendTo("owners-reply-topic")
    public KafkaResponse<CatOwnerDto> save(SaveOwnerRequest request) {
        try {
            if (catOwnerRepository.existsByUserId(request.userId())) {
                return KafkaResponse.error(KafkaResponse.ErrorCode.VALIDATION_ERROR,
                        "Владелец для пользователя с id " + request.userId() + " уже существует");
            }

            CatOwner owner = CatOwnerDtoMapper.toDao(request.owner());
            owner.setUserId(request.userId());
            CatOwner resultOwner = catOwnerRepository.save(owner);
            return KafkaResponse.success(CatOwnerDtoMapper.toDto(resultOwner));
        } catch (Exception e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.INTERNAL_ERROR,
                    "Ошибка при сохранении владельца: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "delete_cat_owner_by_id", groupId = "cat-owner-group")
    @SendTo("owners-reply-topic")
    public KafkaResponse<CatOwnerDto> deleteById(Long id) {
        try {
            CatOwner owner = catOwnerRepository.findById(id)
                    .orElseThrow(() -> new CatOwnerNotFoundException(id));
            catOwnerRepository.delete(owner);
            return KafkaResponse.success(CatOwnerDtoMapper.toDto(owner));
        } catch (CatOwnerNotFoundException e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.NOT_FOUND,
                    "Владелец с id " + id + " не найден");
        } catch (Exception e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.INTERNAL_ERROR,
                    "Ошибка при удалении владельца: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "delete_all_cat_owners", groupId = "cat-owner-group")
    public void deleteAll(String command) {
        catOwnerRepository.deleteAll();
    }

    @KafkaListener(topics = "update_cat_owner", groupId = "cat-owner-group")
    @SendTo("owners-reply-topic")
    public KafkaResponse<CatOwnerDto> update(UpdateOwnerRequest request) {
        try {
            CatOwner owner = catOwnerRepository.findById(request.id())
                    .orElseThrow(() -> new CatOwnerNotFoundException(request.id()));
            if (request.dto().getName() != null)
                owner.setName(request.dto().getName());
            if (request.dto().getBirthDate() != null)
                owner.setBirthDate(request.dto().getBirthDate());
            CatOwner updated = catOwnerRepository.save(owner);
            return KafkaResponse.success(CatOwnerDtoMapper.toDto(updated));
        } catch (CatOwnerNotFoundException e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.NOT_FOUND,
                    "Владелец с id " + request.id() + " не найден");
        } catch (Exception e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.INTERNAL_ERROR,
                    "Ошибка при обновлении владельца: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "get_cat_owner_by_id", groupId = "cat-owner-group")
    @SendTo("owners-reply-topic")
    public KafkaResponse<CatOwnerDto> getById(Long id) {
        try {
            CatOwnerDto owner = catOwnerRepository.findById(id)
                    .map(CatOwnerDtoMapper::toDto)
                    .orElseThrow(() -> new CatOwnerNotFoundException(id));
            return KafkaResponse.success(owner);
        } catch (CatOwnerNotFoundException e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.NOT_FOUND,
                    "Владелец с id " + id + " не найден");
        } catch (Exception e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.INTERNAL_ERROR,
                    "Ошибка при получении владельца: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "get_all_cat_owners", groupId = "cat-owner-group")
    @SendTo("owners-reply-topic")
    public KafkaResponse<OwnerListResponse> getAll(GetAllOwnerRequest request) {
        try {
            Specification<CatOwner> specification = CatOwnerSpecifications.fromFilter(request.filter());
            Page<CatOwner> owners = catOwnerRepository.findAll(specification, request.pageable().toPageable());
            List<CatOwnerDto> ownersList = owners.stream()
                    .map(CatOwnerDtoMapper::toDto)
                    .toList();
            return KafkaResponse.success(new OwnerListResponse(ownersList.toArray(new CatOwnerDto[0])));
        } catch (Exception e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.INTERNAL_ERROR,
                    "Ошибка при получении списка владельцев: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "add_cat_to_owners", groupId = "cat-owner-group")
    @SendTo("owners-reply-topic")
    public KafkaResponse<CatOwnerDto> addCat(AddCatRequest request) {
        try {
            CatOwner owner = catOwnerRepository.findById(request.id())
                    .orElseThrow(() -> new CatOwnerNotFoundException(request.id()));
            return KafkaResponse.success(CatOwnerDtoMapper.toDto(owner));
        } catch (CatOwnerNotFoundException e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.NOT_FOUND,
                    "Владелец с id " + request.id() + " не найден");
        } catch (Exception e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.INTERNAL_ERROR,
                    "Ошибка при проверке владельца: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "check_owner_user", groupId = "cat-owner-group")
    @SendTo("owners-reply-topic")
    public KafkaResponse<Boolean> isOwner(CheckOwnerRequest request) {
        try {
            CatOwner owner = catOwnerRepository.findById(request.ownerId())
                    .orElseThrow(() -> new CatOwnerNotFoundException(request.ownerId()));
            return KafkaResponse.success(owner.getUserId() != null && owner.getUserId().equals(request.userId()));
        } catch (CatOwnerNotFoundException e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.NOT_FOUND,
                    "Владелец с id " + request.ownerId() + " не найден");
        } catch (Exception e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.INTERNAL_ERROR,
                    "Ошибка при проверке владельца: " + e.getMessage());
        }
    }
}
