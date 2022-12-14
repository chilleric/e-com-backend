package com.example.ecom.inventory.permission;

import java.util.Optional;
import com.example.ecom.repository.permission.Permission;

public interface PermissionInventory {
    Optional<Permission> getPermissionByName(String name);

    Optional<Permission> getPermissionById(String id);
}
