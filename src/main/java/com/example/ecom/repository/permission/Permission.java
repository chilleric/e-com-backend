package com.example.ecom.repository.permission;

import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "permissions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Permission {

  private ObjectId _id;
  private String name;
  private List<ObjectId> userId;
  private List<ObjectId> featureId;
  private Date created;
  private Date modified;
  private boolean isCanDelete;
  private int skipAccessability;
  private Map<String, List<String>> viewPoints;
}
