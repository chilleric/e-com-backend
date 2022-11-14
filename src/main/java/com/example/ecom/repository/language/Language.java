package com.example.ecom.repository.language;

import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "languages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Language {
    private ObjectId _id;
    private String language;
    private Map<String, String> dictionary;
}
