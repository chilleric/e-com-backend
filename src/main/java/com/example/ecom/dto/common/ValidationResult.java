package com.example.ecom.dto.common;

import java.util.List;
import java.util.Map;
import com.example.ecom.repository.common_entity.ViewPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationResult {

  private String loginId;
  private Map<String, List<ViewPoint>> viewPoints;
  private Map<String, List<ViewPoint>> editable;

}
