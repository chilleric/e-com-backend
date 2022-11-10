package com.example.ecom.repository.message;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MessageRepository {
    Optional<List<Message>> getMessage(Map<String, String> allParams, String keySort, int page, int pageSize,
            String sortField);

    void insertAndUpdate(Message message);

    void deletePermission(String id);
}
