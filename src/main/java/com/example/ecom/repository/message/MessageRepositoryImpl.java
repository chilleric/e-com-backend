package com.example.ecom.repository.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.example.ecom.repository.AbstractMongoRepo;

@Repository
public class MessageRepositoryImpl extends AbstractMongoRepo implements MessageRepository {

    @Override
    public Optional<List<Message>> getMessage(Map<String, String> allParams, String keySort, int page, int pageSize,
            String sortField) {
        Query query = generateQueryMongoDB(allParams, Message.class, keySort, sortField, page, pageSize);
        return replaceFind(query, Message.class);
    }

    @Override
    public void insertAndUpdate(Message message) {
        authenticationTemplate.save(message, "messages");
    }

    @Override
    public void deletePermission(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("_id", id);
        Query query = generateQueryMongoDB(params, Message.class, "", "", 0, 0);
        authenticationTemplate.remove(query, Message.class);
    }

}
