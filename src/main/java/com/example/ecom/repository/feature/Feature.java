package com.example.ecom.repository.feature;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "features")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feature {
    private ObjectId _id;
    private String name;
    private String path;
    private int deleted;
}
