package com.example.ecom.repository.online_user;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "online_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnlineUser {
    private ObjectId _id;
    private String name;
    private ObjectId userId;
}
