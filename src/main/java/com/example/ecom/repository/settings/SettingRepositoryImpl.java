package com.example.ecom.repository.settings;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.example.ecom.repository.AbstractMongoRepo;

@Repository
public class SettingRepositoryImpl extends AbstractMongoRepo implements SettingRepository {

    @Override
    public Optional<List<Setting>> getSettings(Map<String, String> allParams, String keySort, int page, int pageSize,
            String sortField) {
        Query query = generateQueryMongoDB(allParams, Setting.class, keySort, sortField, page, pageSize);
        return replaceFind(query, Setting.class);
    }

    @Override
    public void insertAndUpdate(Setting setting) {
        authenticationTemplate.save(setting, "settings");
    }

}
