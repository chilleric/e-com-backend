package com.example.ecom.dto.permission;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionResponse {
    private String id;
    private String name;
    private List<String> featureId;
    private List<String> userId;
    private String created;
    private String modified;
    private int skipAccessability;
    private Map<String, List<String>> viewPoints;
}
