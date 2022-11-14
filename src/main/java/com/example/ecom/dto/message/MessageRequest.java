package com.example.ecom.dto.message;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {

    @NotEmpty(message = "Required")
    @NotBlank(message = "Required")
    @NotNull(message = "Required")
    private String message;
}
