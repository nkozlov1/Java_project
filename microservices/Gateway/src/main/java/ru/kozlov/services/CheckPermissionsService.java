package ru.kozlov.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.kozlov.configs.KafkaSender;
import ru.kozlov.models.UserDto;
import ru.kozlov.requests.CheckCatOwnerRequest;
import ru.kozlov.requests.CheckOwnerRequest;
import ru.kozlov.responses.KafkaResponse;

@Service("CheckPermissionsService")
public class CheckPermissionsService {
    private final KafkaSender kafkaSender;
    private final UserService userService;

    @Autowired
    public CheckPermissionsService(KafkaSender kafkaSender, UserService userService) {
        this.kafkaSender = kafkaSender;
        this.userService = userService;
    }

    public boolean isCatOwner(Long catId, Long ownerId) {
        try {
            KafkaResponse<?> response = kafkaSender.sendSync(new CheckCatOwnerRequest(catId, ownerId), "check_cat_owner", "cat-service", Boolean.class);
            if (response.getErrorCode() != null) {
                return false;
            }
            return (Boolean) response.getData();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOwner(Long ownerId, Long userId) {
        try {
            KafkaResponse<?> response = kafkaSender.sendSync(new CheckOwnerRequest(ownerId, userId), "check_owner_user", "owner-service", Boolean.class);
            if (response.getErrorCode() != null) {
                return false;
            }
            return (Boolean) response.getData();
        } catch (Exception e) {
            return false;
        }
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserDto user = userService.findByUsername(username);
        return user.getId();
    }
}
