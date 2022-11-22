package com.example.ecom.service.user;

import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.constant.ResponseType;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.user.UserRequest;
import com.example.ecom.dto.user.UserResponse;
import com.example.ecom.exception.BadSqlException;
import com.example.ecom.exception.ForbiddenException;
import com.example.ecom.exception.InvalidRequestException;
import com.example.ecom.exception.ResourceNotFoundException;
import com.example.ecom.inventory.permission.PermissionInventory;
import com.example.ecom.inventory.user.UserInventory;
import com.example.ecom.repository.accessability.Accessability;
import com.example.ecom.repository.accessability.AccessabilityRepository;
import com.example.ecom.repository.permission.Permission;
import com.example.ecom.repository.permission.PermissionRepository;
import com.example.ecom.repository.user.User;
import com.example.ecom.repository.user.UserRepository;
import com.example.ecom.service.AbstractService;
import com.example.ecom.utils.DateFormat;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends AbstractService<UserRepository> implements UserService {

    @Value("${default.password}")
    protected String defaultPassword;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionInventory permissionInventory;
    @Autowired
    private UserInventory userInventory;

    @Autowired
    private AccessabilityRepository accessabilityRepository;

    @Override
    public void createNewUser(UserRequest userRequest, String loginId) {
        validate(userRequest);
        Map<String, String> error = generateError(UserRequest.class);
        userInventory.findUserByUsername(userRequest.getUsername()).ifPresent(thisName -> {
            error.put("username", LanguageMessageKey.USERNAME_EXISTED);
            throw new InvalidRequestException(error, LanguageMessageKey.USERNAME_EXISTED);
        });
        Date currentTime = DateFormat.getCurrentTime();
        User user = objectMapper.convertValue(userRequest, User.class);
        ObjectId newId = new ObjectId();
        user.set_id(newId);
        user.setPassword(
                bCryptPasswordEncoder().encode(Base64.getEncoder().encodeToString(defaultPassword.getBytes())));
        user.setTokens(new HashMap<>());
        user.setCreated(currentTime);
        user.setModified(currentTime);
        Permission defaultPerm = permissionInventory.getPermissionByName("default_permission").orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.PERMISSION_NOT_FOUND));
        List<ObjectId> userIds = defaultPerm.getUserId();
        userIds.add(newId);
        defaultPerm.setUserId(userIds);
        accessabilityRepository.addNewAccessability(new Accessability(null, new ObjectId(loginId), newId));
        permissionRepository.insertAndUpdate(defaultPerm);
        repository.insertAndUpdate(user);
    }

    public Optional<UserResponse> findOneUserById(String userId, ResponseType type) {
        User user = userInventory.findUserById(userId).orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_USER));
        return Optional.of(new UserResponse(user, type));
    }

    @Override
    public void updateUserById(String userId, UserRequest userRequest) {
        validate(userRequest);
        User user = userInventory.findUserById(userId).orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_USER));
        Map<String, String> error = generateError(UserRequest.class);
        userInventory.findUserByEmail(userRequest.getEmail()).ifPresent(thisEmail -> {
            if (thisEmail.get_id().compareTo(user.get_id()) != 0) {
                error.put("email", LanguageMessageKey.EMAIL_TAKEN);
                throw new InvalidRequestException(error, LanguageMessageKey.EMAIL_TAKEN);
            }
        });
        userInventory.findUserByPhone(userRequest.getPhone()).ifPresent(thisPhone -> {
            if (thisPhone.get_id().compareTo(user.get_id()) != 0) {
                error.put("phone", LanguageMessageKey.PHONE_TAKEN);
                throw new InvalidRequestException(error, LanguageMessageKey.PHONE_TAKEN);
            }
        });
        userInventory.findUserByUsername(userRequest.getUsername()).ifPresent(thisUsername -> {
            if (thisUsername.get_id().compareTo(user.get_id()) != 0) {
                error.put("username", LanguageMessageKey.USERNAME_EXISTED);
                throw new InvalidRequestException(error, LanguageMessageKey.USERNAME_EXISTED);
            }
        });
        if (user.getUsername().compareTo("super_admin") == 0) {
            throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.FORBIDDEN);
        }
        Date currentTime = DateFormat.getCurrentTime();
        User newUser = objectMapper.convertValue(userRequest, User.class);
        newUser.setPassword(user.getPassword());
        newUser.setTokens(user.getTokens());
        newUser.setCreated(user.getCreated());
        newUser.setModified(currentTime);
        newUser.setVerified(user.isVerified());
        newUser.setVerify2FA(user.isVerify2FA());
        newUser.set_id(user.get_id());

        repository.insertAndUpdate(newUser);
    }

    @Override
    public void changeStatusUser(String userId) {
        User user = userInventory.findUserById(userId).orElseThrow(() -> new ResourceNotFoundException(LanguageMessageKey.NOT_FOUND_USER));
        if (user.getUsername().compareTo("super_admin") == 0) {
            throw new InvalidRequestException(new HashMap<>(), LanguageMessageKey.FORBIDDEN);
        }
        user.setDeleted(user.getDeleted() == 0 ? 1 : 0);
        user.setModified(DateFormat.getCurrentTime());

        repository.insertAndUpdate(user);
    }

    @Override
    public Optional<ListWrapperResponse<UserResponse>> getUsers(Map<String, String> allParams, String keySort, int page,
                                                                int pageSize, String sortField, ResponseType type, boolean skipAccessAbility, String loginId) {
        if (type.compareTo(ResponseType.PUBLIC) == 0) {
            allParams.put("deleted", "0");
        }
        List<String> targets = accessabilityRepository.getListTargetId(loginId).orElseThrow(() -> new BadSqlException(LanguageMessageKey.SERVER_ERROR)).stream().map(access -> access.getTargetId().toString()).collect(Collectors.toList());
        if (!skipAccessAbility && allParams.containsKey("_id")) {
            String[] idList = allParams.get("_id").split(",");
            for (int i = 0; i < idList.length; i++) {
                if (!targets.contains(idList[i])) {
                    throw new ForbiddenException(LanguageMessageKey.FORBIDDEN);
                }
            }
        }
        if (!skipAccessAbility && !allParams.containsKey("_id")) {
            allParams.put("_id", generateParamsValue(targets));
            if (targets.size() == 0)
                return Optional.of(new ListWrapperResponse<UserResponse>(new ArrayList<>(), page, pageSize, 0));
        }
        List<User> users = repository.getUsers(allParams, "", page, pageSize, sortField).get();
        return Optional.of(new ListWrapperResponse<UserResponse>(
                users.stream().map(user -> new UserResponse(user, type)).collect(Collectors.toList()),
                page,
                pageSize,
                repository.getTotalPage(allParams)));
    }
}
