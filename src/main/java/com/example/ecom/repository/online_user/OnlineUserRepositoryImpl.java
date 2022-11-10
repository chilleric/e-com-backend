package com.example.ecom.repository.online_user;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.example.ecom.repository.AbstractMongoRepo;

@Repository
public class OnlineUserRepositoryImpl extends AbstractMongoRepo implements OnlineUserRepository {

    @Override
    public Optional<List<OnlineUser>> getOnlineUsers() {
        return replaceFind(new Query(), OnlineUser.class);
    }

    @Override
    public void addOnlineUser(String UserId, String name) {
        try {
            ObjectId user_id = new ObjectId(UserId);
            authenticationTemplate.save(new OnlineUser(null, name, user_id), "online_user");
        } catch (IllegalArgumentException e) {
            APP_LOGGER.error("wrong type user id ");
        }
    }

    @Override
    public void removeOnlineUser(String userId) {
        try {
            ObjectId user_id = new ObjectId(userId);
            Query query = new Query();
            query.addCriteria(Criteria.where("userId").is(user_id));
            authenticationTemplate.remove(query, OnlineUser.class);
        } catch (IllegalArgumentException e) {
            APP_LOGGER.error("wrong type user id ");
        }
    }

}
