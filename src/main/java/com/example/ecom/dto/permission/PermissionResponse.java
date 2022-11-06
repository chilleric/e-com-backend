package com.example.ecom.dto.permission;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionResponse {
    private String id;
    private String name;
    private List<String> features;
    private List<String> users;
    private String created;
    private String modified;
    private int skipAccessability;
}
