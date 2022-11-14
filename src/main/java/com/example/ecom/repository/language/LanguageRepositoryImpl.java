package com.example.ecom.repository.language;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.example.ecom.repository.AbstractMongoRepo;

@Repository
public class LanguageRepositoryImpl extends AbstractMongoRepo implements LanguageRepository {

    @Override
    public Optional<List<Language>> getLanguages(Map<String, String> allParams, String keySort, int page, int pageSize,
            String sortField) {
        Query query = generateQueryMongoDB(allParams, Language.class, keySort, sortField, page, pageSize);
        return replaceFind(query, Language.class);
    }

    @Override
    public void insertAndUpdate(Language language) {
        authenticationTemplate.save(language, "languages");
    }

    @Override
    public long getTotal(Map<String, String> allParams) {
        Query query = generateQueryMongoDB(allParams, Language.class, "", "", 0, 0);
        long total = authenticationTemplate.count(query, Language.class);
        return total;
    }

}
