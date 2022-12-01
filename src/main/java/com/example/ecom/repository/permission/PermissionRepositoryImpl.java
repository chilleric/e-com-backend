package com.example.ecom.repository.permission;

import static java.util.Map.entry;

import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.dto.user.UserResponse;
import com.example.ecom.exception.BadSqlException;
import com.example.ecom.repository.AbstractMongoRepo;
import com.example.ecom.repository.common_entity.ViewPoint;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
  public Map<String, List<ViewPoint>> getViewPointSelect() {
    List<Class<?>> viewPointList = List.of(UserResponse.class);
    Map<String, List<ViewPoint>> result = new HashMap<>();
    viewPointList.forEach(clazz -> {
      List<ViewPoint> attributes = new ArrayList<>();
      for (Field field : clazz.getDeclaredFields()) {
        attributes.add(new ViewPoint(field.getName(), field.getName()));
      }
      result.put(clazz.getSimpleName(), attributes);
    });
    return removeId(result);
  }

  private Map<String, List<ViewPoint>> removeId(Map<String, List<ViewPoint>> thisView) {
    return thisView.entrySet().stream().map((key) -> {
      List<ViewPoint> newValue = key.getValue().stream()
          .filter(viewList -> viewList.getKey().compareTo("id") != 0).collect(
              Collectors.toList());
      return entry(key.getKey(), newValue);
    }).collect(
        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
  }

  @Override
  public Optional<List<Permission>> getPermissionByUserId(String userId) {
    try {
      ObjectId user_id = new ObjectId(userId);
      Query query = new Query();
      query.addCriteria(Criteria.where("userId").in(user_id));
      return replaceFind(query, Permission.class);
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
