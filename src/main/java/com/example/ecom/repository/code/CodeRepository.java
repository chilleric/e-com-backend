package com.example.ecom.repository.code;

import java.util.List;
import java.util.Optional;

public interface CodeRepository {
    Optional<List<Code>> getCodesByCode(String userId, String code);

    Optional<List<Code>> getCodesByType(String userId, String type);

    void insertAndUpdateCode(Code code);
}
