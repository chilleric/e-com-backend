package com.example.ecom.repository.userRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository {
    Optional<List<User>> getUsers(Map<String, String> allParams, String keySort, int page, int pageSize,
                        String sortField);

    void insertAndUpdate(User user);
    long getTotalPage(Map<String, String> allParams);
}
