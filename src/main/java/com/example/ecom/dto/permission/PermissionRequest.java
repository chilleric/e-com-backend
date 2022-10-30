package com.example.ecom.dto.permission;

import java.util.List;

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
    @NotBlank(message = "Name is required!")
    @NotNull(message = "Name is required!")
    @NotEmpty(message = "Name is required!")
    private String name;

    @NotNull(message = "Feature id is required!")
    @NotEmpty(message = "Feature id is required!")
    private List<String> featureId;

    @NotNull(message = "User id is required!")
    @NotEmpty(message = "User id is required!")
    private List<String> userId;

    @NotNull(message = "Skip accessability id is required!")
    @Min(value = 0, message = "Min value is 0 (true)")
    @Max(value = 1, message = "Max value is 1 (false)")
    private int skipAccessability;
}
