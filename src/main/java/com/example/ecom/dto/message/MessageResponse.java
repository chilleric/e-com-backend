package com.example.ecom.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private String id;
    private String sendId;
    private String receiveId;
    private String context;
    private String created;
}
