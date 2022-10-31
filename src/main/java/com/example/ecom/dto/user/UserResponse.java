package com.example.ecom.dto.user;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.example.ecom.constant.DateTime;
import com.example.ecom.constant.ResponseType;
import com.example.ecom.repository.user.User;
import com.example.ecom.utils.DateFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private String password;
    private int gender;
    private String dob;
    private String address;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Map<String, Date> tokens;
    private String created;
    private String modified;
    private boolean verified;
    private boolean verify2FA;
    private int deleted;

    public UserResponse(User user, ResponseType responseType) {
        this.id = user.get_id().toString();
        this.username = responseType.getResponseType().compareTo("public") == 0 ? "" : user.getUsername();
        this.password = responseType.getResponseType().compareTo("public") == 0 ? "" : user.getPassword();
        this.gender = user.getGender();
        this.dob = responseType.getResponseType().compareTo("public") == 0 ? "" : user.getDob();
        this.address = responseType.getResponseType().compareTo("public") == 0 ? "" : user.getAddress();
        this.lastName = responseType.getResponseType().compareTo("public") == 0 ? "" : user.getLastName();
        this.firstName = user.getFirstName();
        this.email = responseType.getResponseType().compareTo("public") == 0 ? "" : user.getEmail();
        this.phone = responseType.getResponseType().compareTo("public") == 0 ? "" : user.getPhone();
        this.tokens = responseType.getResponseType().compareTo("public") == 0 ? new HashMap<>() : user.getTokens();
        this.created = DateFormat.toDateString(user.getCreated(), DateTime.YYYY_MM_DD);
        this.modified = responseType.getResponseType().compareTo("public") == 0 ? ""
                : DateFormat.toDateString(user.getModified(), DateTime.YYYY_MM_DD);
        this.verified = responseType.getResponseType().compareTo("public") == 0 ? false : user.isVerified();
        this.verify2FA = responseType.getResponseType().compareTo("public") == 0 ? false : user.isVerify2FA();
        this.deleted = responseType.getResponseType().compareTo("public") == 0 ? 0 : user.getDeleted();
    }

}
