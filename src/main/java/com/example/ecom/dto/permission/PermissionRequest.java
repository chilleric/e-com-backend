package com.example.ecom.dto.permission;

import com.example.ecom.constant.LanguageMessageKey;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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

  @NotNull(message = LanguageMessageKey.SKIP_ACCESS_REQUIRED)
  @Min(value = 0, message = LanguageMessageKey.ONLY_0_1)
  @Max(value = 1, message = LanguageMessageKey.ONLY_0_1)
  private int skipAccessability;

  @NotNull(message = LanguageMessageKey.VIEW_POINT_REQUIRED)
  private Map<String, List<String>> viewPoints;

}
