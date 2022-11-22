package com.example.ecom.inventory.permission;

import com.example.ecom.repository.permission.Permission;

import java.util.Optional;

public interface PermissionInventory {
    Optional<Permission> getPermissionByName(String name);

    Optional<Permission> getPermissionById(String id);
}
