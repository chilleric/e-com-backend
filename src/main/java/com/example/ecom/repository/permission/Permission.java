package com.example.ecom.repository.permission;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import com.example.ecom.repository.common_entity.ViewPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "permissions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Permission {

  private ObjectId _id;
  private String name;
  private List<ObjectId> userId;
  private Date created;
  private Date modified;
  private Map<String, List<ViewPoint>> viewPoints;
  private Map<String, List<ViewPoint>> editable;
}
