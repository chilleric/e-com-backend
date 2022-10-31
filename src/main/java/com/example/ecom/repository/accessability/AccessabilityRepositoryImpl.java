package com.example.ecom.repository.accessability;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.example.ecom.repository.AbstractMongoRepo;

@Repository
public class AccessabilityRepositoryImpl extends AbstractMongoRepo implements AccessabilityRepository {
    @Override
    public Optional<Accessability> getAccessability(String userId, String targetId) {
        try {
            ObjectId user_id = new ObjectId(userId);
            ObjectId target_id = new ObjectId(targetId);
            Query query = new Query();
            query.addCriteria(Criteria.where("userId").is(user_id).and("targetId").in(target_id));
            return replaceFindOne(query, Accessability.class);
        } catch (IllegalArgumentException e) {
            APP_LOGGER.error("wrong type user id");
            return Optional.empty();
        }
    }

    @Override
    public void addNewAccessability(Accessability accessability) {
        authenticationTemplate.save(accessability);
    }

    @Override
    public void deleteAccessability(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("_id", id);
        Query query = generateQueryMongoDB(params, Accessability.class, "", "", 0, 0);
        authenticationTemplate.remove(query, Accessability.class);
    }
}
