package com.example.ecom.dto.permission;

import java.util.List;

import com.example.ecom.dto.feature.FeatureResponse;
import com.example.ecom.dto.user.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionResponse {
    private String _id;
    private String name;
    private List<FeatureResponse> features;
    private List<UserResponse> users;
    private String created;
    private String modified;
    private int skipAccessability;
}
