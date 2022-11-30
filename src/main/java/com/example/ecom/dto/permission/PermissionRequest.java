package com.example.ecom.dto.permission;

import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.repository.common_entity.ViewPoint;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionRequest {

  @NotBlank(message = LanguageMessageKey.PERMISSION_NAME_REQUIRED)
  @NotNull(message = LanguageMessageKey.PERMISSION_NAME_REQUIRED)
  @NotEmpty(message = LanguageMessageKey.PERMISSION_NAME_REQUIRED)
  private String name;

  @NotNull(message = LanguageMessageKey.FEATURE_LIST_REQUIRED)
  private List<String> featureId;

  @NotNull(message = LanguageMessageKey.USER_LIST_REQUIRED)
  private List<String> userId;

  @NotNull(message = LanguageMessageKey.VIEW_POINT_REQUIRED)
  private Map<String, List<ViewPoint>> viewPoints;

  @NotNull(message = LanguageMessageKey.VIEW_POINT_REQUIRED)
  private Map<String, List<ViewPoint>> editable;

}
