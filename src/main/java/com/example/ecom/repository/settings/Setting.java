package com.example.ecom.repository.settings;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "settings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Setting {
    private ObjectId _id;
    private ObjectId userId;
    private boolean darkTheme;
}
