package com.example.ecom.dto.common;

import java.util.List;
import com.example.ecom.repository.common_entity.ViewPoint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> {

  private boolean success;
  private T result;
  private String message;
  private int statusCode;
  private List<ViewPoint> viewPoints;
  private List<ViewPoint> editable;
}
