package com.example.ecom.dto.message;

import java.util.Date;

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
    private Date created;
}
