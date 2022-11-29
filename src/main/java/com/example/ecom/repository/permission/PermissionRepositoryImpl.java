package com.example.ecom.repository.permission;

import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.dto.permission.PermissionResponse;
import com.example.ecom.dto.user.UserResponse;
import com.example.ecom.exception.BadSqlException;
import com.example.ecom.repository.AbstractMongoRepo;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionRepositoryImpl extends AbstractMongoRepo implements PermissionRepository {

  @Override
  public Optional<List<Permission>> getPermissions(Map<String, String> allParams, String keySort,
      int page,
      int pageSize, String sortField) {
    Query query = generateQueryMongoDB(allParams, Permission.class, keySort, sortField, page,
        pageSize);
    return replaceFind(query, Permission.class);
  }

  @Override
  public Optional<Permission> getPermissionById(String id) {
    try {
      ObjectId _id = new ObjectId(id);
      Query query = new Query();
      query.addCriteria(Criteria.where("_id").is(_id));
      return replaceFindOne(query, Permission.class);
    } catch (IllegalArgumentException e) {
      APP_LOGGER.error("wrong type_id");
      return Optional.empty();
    }
  }

  @Override
  public void insertAndUpdate(Permission permission) {
    authenticationTemplate.save(permission, "permissions");
  }

  @Override
  public void deletePermission(String id) {
    try {
      ObjectId _id = new ObjectId(id);
      Query query = new Query();
      query.addCriteria(Criteria.where("_id").is(_id));
      authenticationTemplate.remove(query, Permission.class);
    } catch (IllegalArgumentException e) {
      APP_LOGGER.error("wrong type_id");
      throw new BadSqlException(LanguageMessageKey.SERVER_ERROR);
    }
  }

  @Override
  public Map<String, List<String>> getViewPointSelect() {
    List<Class<?>> viewPointList = List.of(UserResponse.class, PermissionResponse.class);
    Map<String, List<String>> result = new HashMap<>();
    viewPointList.forEach(clazz -> {
      List<String> attributes = new ArrayList<>();
      for (Field field : clazz.getDeclaredFields()) {
        attributes.add(field.getName());
      }
      result.put(clazz.getSimpleName(), attributes);
    });
    return result;
  }

  @Override
  public Optional<Permission> getPermissionByUser(String userId, String featureId) {
    try {
      ObjectId user_id = new ObjectId(userId);
      ObjectId feature_id = new ObjectId(featureId);
      Query query = new Query();
      query.addCriteria(Criteria.where("userId").in(user_id).and("featureId").in(feature_id));
      return replaceFindOne(query, Permission.class);
    } catch (IllegalArgumentException e) {
      APP_LOGGER.error("wrong type user id or feature id");
      return Optional.empty();
    }
  }

  @Override
  public Optional<Permission> getPermissionByUserId(String userId) {
    try {
      ObjectId user_id = new ObjectId(userId);
      Query query = new Query();
      query.addCriteria(Criteria.where("userId").in(user_id));
      return replaceFindOne(query, Permission.class);
    } catch (IllegalArgumentException e) {
      APP_LOGGER.error("wrong type user id or feature id");
      return Optional.empty();
    }
  }

  @Override
  public long getTotal(Map<String, String> allParams) {
    Query query = generateQueryMongoDB(allParams, Permission.class, "", "", 0, 0);
    return authenticationTemplate.count(query, Permission.class);
  }

}
