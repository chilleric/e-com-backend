package com.example.ecom.dto.permission;

import java.util.List;
import java.util.Map;
import com.example.ecom.repository.common_entity.ViewPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionResponse {

  private String id;
  private String name;
  private List<String> userId;
  private String created;
  private String modified;
  private Map<String, List<ViewPoint>> viewPoints;
  private Map<String, List<ViewPoint>> editable;
}
