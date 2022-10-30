package com.example.ecom.repository.accessability;

import java.util.Optional;

public interface AccessabilityRepository {
    Optional<Accessability> getAccessability(String userId, String targetId);

    void addNewAccessability(Accessability accessability);

    void deleteAccessability(String id);
}
