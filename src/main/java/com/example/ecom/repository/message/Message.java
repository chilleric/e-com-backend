package com.example.ecom.repository.message;

import java.util.Date;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private ObjectId _id;
    private ObjectId sendId;
    private ObjectId receiveId;
    private String context;
    private Date create;
}
