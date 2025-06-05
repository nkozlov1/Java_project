package ru.kozlov.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import ru.kozlov.dtos.CatOwnerDto;
import ru.kozlov.exceptions.CatNotFoundException;
import ru.kozlov.requests.*;
import ru.kozlov.dtos.CatDto;
import ru.kozlov.models.Cat;
import ru.kozlov.models.CatDtoMapper;
import ru.kozlov.models.CatSpecifications;
import ru.kozlov.models.ColorsDtoMapper;
import ru.kozlov.repositories.CatRepository;
import ru.kozlov.responses.CatListResponse;
import ru.kozlov.responses.KafkaResponse;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service("catService")
public class CatServiceImpl implements CatService {
    private final CatRepository catRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ReplyingKafkaTemplate<String, Object, Object> replyingKafka;

    @Autowired
    public CatServiceImpl(CatRepository catRepository, KafkaTemplate<String, Object> kafkaTemplate, ReplyingKafkaTemplate<String, Object, Object> replyingKafka) {
        this.catRepository = catRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.replyingKafka = replyingKafka;
    }


    @KafkaListener(topics = "save_cat", groupId = "cat-group")
    @SendTo("cats-reply-topic")
    public KafkaResponse<CatDto> save(CatDto catDto) {
        try {
            Cat cat = CatDtoMapper.toDao(catDto);
            Cat resultCat = catRepository.save(cat);
            return KafkaResponse.success(CatDtoMapper.toDto(resultCat));
        } catch (Exception e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.INTERNAL_ERROR,
                    "Ошибка при сохранении кота: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "delete_cat_by_id", groupId = "cat-group")
    @SendTo("cats-reply-topic")
    public KafkaResponse<CatDto> deleteById(Long id) {
        try {
            Cat cat = catRepository.findById(id)
                    .orElseThrow(() -> new CatNotFoundException(id));
            cat.getFriends().clear();
            catRepository.saveAll(cat.getFriends());
            catRepository.delete(cat);
            return KafkaResponse.success(CatDtoMapper.toDto(cat));
        } catch (CatNotFoundException e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.NOT_FOUND,
                    "Кот с id " + id + " не найден");
        } catch (Exception e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.INTERNAL_ERROR,
                    "Ошибка при удалении кота: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "delete_all_cat", groupId = "cat-group")
    public void deleteAll(String command) {
        catRepository.deleteAll();
    }

    @KafkaListener(topics = "update_cat", groupId = "cat-group")
    @SendTo("cats-reply-topic")
    public KafkaResponse<CatDto> update(UpdateCatRequest request) {
        try {
            Cat cat = catRepository.findById(request.id())
                    .orElseThrow(() -> new CatNotFoundException(request.id()));
            if (request.dto().getName() != null)
                cat.setName(request.dto().getName());
            if (request.dto().getColor() != null)
                cat.setColor(ColorsDtoMapper.toDao(request.dto().getColor()));
            if (request.dto().getBirthDate() != null)
                cat.setBirthDate(request.dto().getBirthDate());
            if (request.dto().getBreed() != null)
                cat.setBreed(request.dto().getBreed());
            Cat updated = catRepository.save(cat);
            return KafkaResponse.success(CatDtoMapper.toDto(updated));
        } catch (CatNotFoundException e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.NOT_FOUND,
                    "Кот с id " + request.id() + " не найден");
        } catch (Exception e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.INTERNAL_ERROR,
                    "Ошибка при обновлении кота: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "get_cat_by_id", groupId = "cat-group")
    @SendTo("cats-reply-topic")
    public KafkaResponse<CatDto> getById(Long id) {
        try {
            CatDto cat = catRepository.findById(id)
                    .map(CatDtoMapper::toDto)
                    .orElseThrow(() -> new CatNotFoundException(id));
            return KafkaResponse.success(cat);
        } catch (CatNotFoundException e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.NOT_FOUND,
                    "Кот с id " + id + " не найден");
        } catch (Exception e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.INTERNAL_ERROR,
                    "Ошибка при получении кота: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "get_all_cat", groupId = "cat-group")
    @SendTo("cats-reply-topic")
    public KafkaResponse<CatListResponse> getAll(GetAllCatsRequest request) {
        try {
            Specification<Cat> specification = CatSpecifications.fromFilter(request.filter());
            Page<Cat> cats = catRepository.findAll(specification, request.pageable().toPageable());
            List<CatDto> catsList = cats.stream()
                    .map(CatDtoMapper::toDto)
                    .toList();
            return KafkaResponse.success(new CatListResponse(catsList));
        } catch (Exception e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.INTERNAL_ERROR,
                    "Ошибка при получении списка котов: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "get_all_cat_friends", groupId = "cat-group")
    @SendTo("cats-reply-topic")
    public KafkaResponse<CatListResponse> getAllFriends(Long id) {
        try {
            Cat cat = catRepository.findById(id)
                    .orElseThrow(() -> new CatNotFoundException(id));
            Set<Cat> friends = cat.getFriends();
            List<CatDto> cats = friends.stream()
                    .map(CatDtoMapper::toDto)
                    .toList();
            return KafkaResponse.success(new CatListResponse(cats));
        } catch (CatNotFoundException e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.NOT_FOUND,
                    "Кот с id " + id + " не найден");
        } catch (Exception e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.INTERNAL_ERROR,
                    "Ошибка при получении друзей кота: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "get_cat_by_owner_id", groupId = "cat-group")
    @SendTo("cats-reply-topic")
    public KafkaResponse<CatListResponse> getAllCatsById(Long id) {
        try {
            List<Cat> cats = catRepository.findCatsByOwnerId(id);
            List<CatDto> catList = cats.stream()
                    .map(CatDtoMapper::toDto)
                    .toList();
            return KafkaResponse.success(new CatListResponse(catList));
        } catch (Exception e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.INTERNAL_ERROR,
                    "Ошибка при получении котов владельца: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "add_cat_friend", groupId = "cat-group")
    @SendTo
    public KafkaResponse<CatDto> addFriend(MakeFriendRequest request) {
        try {
            Cat cat = catRepository.findById(request.catId())
                    .orElseThrow(() -> new CatNotFoundException(request.catId()));
            Cat friend = catRepository.findById(request.friendId())
                    .orElseThrow(() -> new CatNotFoundException(request.friendId()));
            cat.addFriend(friend);
            friend.addFriend(cat);
            catRepository.save(cat);
            catRepository.save(friend);
            return KafkaResponse.success(CatDtoMapper.toDto(cat));
        } catch (CatNotFoundException e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.NOT_FOUND,
                    "Один из котов не найден: " + e.getMessage());
        } catch (Exception e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.INTERNAL_ERROR,
                    "Ошибка при добавлении друга: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "add_owner_to_cat", groupId = "cat-group")
    @SendTo("cats-reply-topic")
    public KafkaResponse<CatDto> addOwner(AddCatRequest request) {
        try {
            final ConsumerRecord<String, Object> consumerRecord;
            try {
                ProducerRecord<String, Object> record = new ProducerRecord<>("add_cat_to_owners", request);
                RequestReplyFuture<String, Object, Object> replyFuture = replyingKafka.sendAndReceive(record);
                consumerRecord = replyFuture.get(10, TimeUnit.SECONDS);
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                return KafkaResponse.error(KafkaResponse.ErrorCode.INTERNAL_ERROR, e.getMessage());
            }
            Object reply = consumerRecord.value();
            KafkaResponse<CatOwnerDto> ownerResponse = (KafkaResponse<CatOwnerDto>) reply;
            if (ownerResponse.getErrorCode() != null) {
                return KafkaResponse.error(ownerResponse.getErrorCode(), ownerResponse.getErrorMessage());
            }
            Cat cat = catRepository.findById(request.catId())
                    .orElseThrow(() -> new CatNotFoundException(request.catId()));
            cat.setOwnerId(request.id());
            Cat savedCat = catRepository.save(cat);
            return KafkaResponse.success(CatDtoMapper.toDto(savedCat));
        } catch (CatNotFoundException e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.NOT_FOUND,
                    "Кот с id " + request.catId() + " не найден");
        } catch (Exception e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.INTERNAL_ERROR,
                    "Ошибка при добавлении владельца: " + e.getMessage());
        }
    }

    @KafkaListener(topics = "check_cat_owner", groupId = "cat-group")
    @SendTo("cats-reply-topic")
    public KafkaResponse<Boolean> isCatOwner(CheckCatOwnerRequest request) {
        try {
            Cat cat = catRepository.findById(request.catId())
                    .orElseThrow(() -> new CatNotFoundException(request.catId()));
            return KafkaResponse.success(cat.getOwnerId() != null && cat.getOwnerId().equals(request.ownerId()));
        } catch (CatNotFoundException e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.NOT_FOUND,
                    "Кот с id " + request.catId() + " не найден");
        } catch (Exception e) {
            return KafkaResponse.error(KafkaResponse.ErrorCode.INTERNAL_ERROR,
                    "Ошибка при проверке владельца кота: " + e.getMessage());
        }
    }
}
