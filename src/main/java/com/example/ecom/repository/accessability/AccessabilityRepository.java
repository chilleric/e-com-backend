package com.example.ecom.repository.accessability;

import java.util.List;
import java.util.Optional;

public interface AccessabilityRepository {
    Optional<Accessability> getAccessability(String userId, String targetId);

    Optional<List<Accessability>> getListTargetId(String userId);

    void addNewAccessability(Accessability accessability);

    void deleteAccessability(String id);
}
