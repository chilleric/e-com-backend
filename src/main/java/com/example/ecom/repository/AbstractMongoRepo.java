package com.example.ecom.repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.example.ecom.exception.BadSqlException;
import com.example.ecom.log.AppLogger;
import com.example.ecom.log.LoggerFactory;
import com.example.ecom.log.LoggerType;

public abstract class AbstractMongoRepo {

    @Autowired
    @Qualifier("mongo_template")
    protected MongoTemplate authenticationTemplate;

    protected AppLogger APP_LOGGER = LoggerFactory.getLogger(LoggerType.APPLICATION);

    protected Query generateQueryMongoDB(Map<String, String> allParams, Class<?> clazz, String keySort,
            String sortField, int page, int pageSize) {
        Query query = new Query();
        Field[] fields = clazz.getDeclaredFields();
        Criteria criteria = new Criteria();
        for (Map.Entry<String, String> items : allParams.entrySet()) {
            for (Field field : fields) {
                if (field.getName().compareTo(items.getKey()) == 0) {
                    String[] values = items.getValue().split(",");
                    if (field.getType() == ObjectId.class) {
                        List<Criteria> multipleCriteria = new ArrayList<>();
                        for (int i = 0; i < values.length; i++) {
                            try {
                                multipleCriteria.add(Criteria.where(items.getKey()).is(new ObjectId(values[i])));
                            } catch (IllegalArgumentException e) {
                                APP_LOGGER.error(e.getMessage());
                                throw new BadSqlException("id must be objectId format!");
                            }
                        }
                        criteria.orOperator(multipleCriteria);
                    }
                    if (field.getType() == Boolean.class) {
                        List<Criteria> multipleCriteria = new ArrayList<>();
                        for (int i = 0; i < values.length; i++) {
                            try {
                                boolean value = Boolean.parseBoolean(values[i]);
                                multipleCriteria.add(Criteria.where(items.getKey()).is(value));
                            } catch (Exception e) {
                                APP_LOGGER.error("error parsing value boolean");
                            }
                        }
                        criteria.orOperator(multipleCriteria);
                    }
                    if (field.getType() == int.class) {
                        List<Criteria> multipleCriteria = new ArrayList<>();
                        for (int i = 0; i < values.length; i++) {
                            try {
                                int value = Integer.parseInt(values[i]);
                                multipleCriteria.add(Criteria.where(items.getKey()).is(value));
                            } catch (Exception e) {
                                APP_LOGGER.error("error parsing value int");
                            }
                        }
                        criteria.orOperator(multipleCriteria);
                    }
                    if (field.getType() == String.class) {
                        List<Criteria> multipleCriteria = new ArrayList<>();
                        for (int i = 0; i < values.length; i++) {
                            multipleCriteria.add(Criteria.where(items.getKey()).is(values[i]));
                        }
                        criteria.orOperator(multipleCriteria);
                    }
                }
            }
        }
        query.addCriteria(criteria);
        if (keySort.trim().compareTo("") != 0 && sortField.trim().compareTo("ASC") != 0) {
            query.with(Sort.by(Sort.Direction.ASC, sortField));
        }
        if (keySort.trim().compareTo("") != 0 && sortField.trim().compareTo("DESC") != 0) {
            query.with(Sort.by(Sort.Direction.DESC, sortField));
        }
        if (page > 0 && pageSize > 0) {
            Pageable pageable = PageRequest.of(page - 1, pageSize);
            query.with(pageable);
        }
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
