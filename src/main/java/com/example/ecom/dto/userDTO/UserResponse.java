package com.example.ecom.dto.userDTO;

import java.util.Date;
import java.util.Map;

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
}
