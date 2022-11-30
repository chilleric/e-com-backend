package com.example.ecom.repository.permission;

import com.example.ecom.repository.common_entity.ViewPoint;
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
  private Date created;
  private Date modified;
  private Map<String, List<ViewPoint>> viewPoints;
  private Map<String, List<ViewPoint>> editable;
}
