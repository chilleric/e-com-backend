package com.example.ecom.repository.userRepository;

import java.util.Date;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private ObjectId _id;
    private String username;
    private String password;
    private int gender;
    private String dob;
    private String address;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Map<String, Long> tokens;
    private Date created;
    private Date modified;
    private boolean verified;
    private boolean verify2FA;
    private int deleted;
}
