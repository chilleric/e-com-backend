package com.example.ecom.repository.settings;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SettingRepository {
    Optional<List<Setting>> getSettings(Map<String, String> allParams, String keySort, int page, int pageSize,
            String sortField);

    void insertAndUpdate(Setting setting);

}
