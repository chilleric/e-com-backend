package com.example.ecom.dto.message;

import com.example.ecom.constant.LanguageMessageKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {

    @NotEmpty(message = LanguageMessageKey.MESSAGE_REQUIRED)
    @NotBlank(message = LanguageMessageKey.MESSAGE_REQUIRED)
    @NotNull(message = LanguageMessageKey.MESSAGE_REQUIRED)
    private String message;
}
