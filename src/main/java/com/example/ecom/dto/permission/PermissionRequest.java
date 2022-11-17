package com.example.ecom.dto.permission;

import com.example.ecom.constant.LanguageMessageKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;

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
}
