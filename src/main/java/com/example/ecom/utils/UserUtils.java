package com.example.ecom.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.ecom.constant.DateTime;
import com.example.ecom.dto.userDTO.UserResponse;
import com.example.ecom.repository.userRepository.User;

@Component
public class UserUtils {

    public UserResponse generateUserResponse(User user, String type) {
        Map<String, Date> defaultTokens = new HashMap<>();
        if (type.compareTo("public") == 0) {
            return new UserResponse(
                    user.get_id().toString(),
                    "",
                    "",
                    user.getGender(),
                    "",
                    "",
                    user.getFirstName(),
                    user.getLastName(),
                    "",
                    "",
                    defaultTokens,
                    "",
                    "",
                    false,
                    false,
                    0);
        } else {
            return new UserResponse(
                    user.get_id().toString(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getGender(),
                    user.getDob(),
                    user.getAddress(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getTokens(),
                    DateFormat.toDateString(user.getCreated(), DateTime.YYYY_MM_DD),
                    DateFormat.toDateString(user.getModified(), DateTime.YYYY_MM_DD),
                    user.isVerified(),
                    user.isVerify2FA(),
                    user.getDeleted());

        }
    }
}
