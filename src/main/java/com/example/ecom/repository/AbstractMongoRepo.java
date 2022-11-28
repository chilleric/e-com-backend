package com.example.ecom.repository;

import com.example.ecom.log.AppLogger;
import com.example.ecom.log.LoggerFactory;
import com.example.ecom.log.LoggerType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public abstract class AbstractMongoRepo {

  @Autowired
  @Qualifier("mongo_template")
  protected MongoTemplate authenticationTemplate;

  protected AppLogger APP_LOGGER = LoggerFactory.getLogger(LoggerType.APPLICATION);

  protected static boolean isHasOperator = false;

  protected Query generateQueryMongoDB(Map<String, String> allParams, Class<?> clazz,
      String keySort,
      String sortField, int page, int pageSize) {
    Query query = new Query();
    Field[] fields = clazz.getDeclaredFields();
    List<Criteria> allCriteria = new ArrayList<>();
    int isSort = 0;
    for (Map.Entry<String, String> items : allParams.entrySet()) {
      for (Field field : fields) {
        if (field.getName().compareTo(sortField) == 0) {
          isSort = 1;
        }
        if (field.getName().compareTo(items.getKey()) == 0) {
          String[] values = items.getValue().split(",");
          List<Criteria> multipleCriteria = new ArrayList<>();
          if (field.getType() == ObjectId.class) {
            for (String value : values) {
              try {
                multipleCriteria.add(
                    Criteria.where(items.getKey()).is(new ObjectId(value)));
                isHasOperator = true;
              } catch (IllegalArgumentException e) {
                APP_LOGGER.error(e.getMessage());
              }
            }
          }
          if (field.getType() == Boolean.class) {
            for (String s : values) {
              try {
                boolean value = Boolean.parseBoolean(s);
                multipleCriteria.add(Criteria.where(items.getKey()).is(value));
                isHasOperator = true;
              } catch (Exception e) {
                APP_LOGGER.error("error parsing value boolean");
              }
            }
          }
          if (field.getType() == int.class) {
            for (String s : values) {
              try {
                int value = Integer.parseInt(s);
                multipleCriteria.add(Criteria.where(items.getKey()).is(value));
                isHasOperator = true;
              } catch (Exception e) {
                APP_LOGGER.error("error parsing value int");
              }
            }
          }
          if (field.getType() == String.class) {
            for (String value : values) {
              multipleCriteria.add(Criteria.where(items.getKey()).is(value));
              isHasOperator = true;
            }
          }
          if (isHasOperator) {
            allCriteria.add(new Criteria().orOperator(multipleCriteria));
          }
        }
      }
    }
    if (allCriteria.size() > 0 && isHasOperator) {
      query.addCriteria(new Criteria().andOperator(allCriteria));
    }
    if (isSort == 1 && keySort.trim().compareTo("") != 0 && keySort.trim().compareTo("ASC") == 0) {
      query.with(Sort.by(Sort.Direction.ASC, sortField));
    }
    if (isSort == 1 && keySort.trim().compareTo("") != 0 && keySort.trim().compareTo("DESC") == 0) {
      query.with(Sort.by(Sort.Direction.DESC, sortField));
    }
    if (page > 0 && pageSize > 0) {
      query.skip((long) (page - 1) * pageSize).limit(pageSize);
    }
    isHasOperator = false;
    return query;
  }

  protected <T> Optional<List<T>> replaceFind(Query query, Class<T> clazz) {
    try {
      List<T> result = authenticationTemplate.find(query, clazz);
      return Optional.of(result);
    } catch (IllegalArgumentException e) {
      APP_LOGGER.error(e.getMessage());
      return Optional.empty();
    } catch (NullPointerException e) {
      APP_LOGGER.error(e.getMessage());
      return Optional.empty();
    }
  }

  protected <T> Optional<T> replaceFindOne(Query query, Class<T> clazz) {
    try {
      T result = authenticationTemplate.findOne(query, clazz);
      return Optional.of(result);
    } catch (IllegalArgumentException e) {
      APP_LOGGER.error(e.getMessage());
      return Optional.empty();
    } catch (NullPointerException e) {
      APP_LOGGER.error(e.getMessage());
      return Optional.empty();
    }
  }

}
