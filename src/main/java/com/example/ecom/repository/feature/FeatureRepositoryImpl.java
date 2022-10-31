package com.example.ecom.repository.feature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.example.ecom.repository.AbstractMongoRepo;

@Repository
public class FeatureRepositoryImpl extends AbstractMongoRepo implements FeatureRepository {
    @Override
    public Optional<List<Feature>> getFeatures(Map<String, String> allParams, String keySort, int page, int pageSize,
            String sortField) {
        Query query = generateQueryMongoDB(allParams, Feature.class, keySort, sortField, page, pageSize);
        return replaceFind(query, Feature.class);
    }

    @Override
    public void insertAndUpdate(Feature feature) {
        authenticationTemplate.save(feature, "features");
    }

    @Override
    public void deleteFeature(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("_id", id);
        Query query = generateQueryMongoDB(params, Feature.class, "", "", 0, 0);
        authenticationTemplate.remove(query, Feature.class);
    }

    @Override
    public long getTotal(Map<String, String> allParams) {
        Query query = generateQueryMongoDB(allParams, Feature.class, "", "", 0, 0);
        long total = authenticationTemplate.count(query, Feature.class);
        return total;
    }
}
