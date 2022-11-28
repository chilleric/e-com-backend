package com.example.ecom.dto.common;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationResult {

  private boolean skipAccessability;
  private String loginId;
  private Map<String, List<String>> viewPoints;
}
