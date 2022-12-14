package com.example.ecom.inventory.permission;

import static java.util.Map.entry;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.example.ecom.inventory.AbstractInventory;
import com.example.ecom.repository.permission.Permission;
import com.example.ecom.repository.permission.PermissionRepository;

@Service
public class PermissionInventoryImpl extends AbstractInventory<PermissionRepository>
        implements PermissionInventory {
    @Override
    public Optional<Permission> getPermissionByName(String name) {
        List<Permission> permissions =
                repository.getPermissions(Map.ofEntries(entry("name", name)), "", 0, 0, "").get();
        if (permissions.size() != 0)
            return Optional.of(permissions.get(0));
        return Optional.empty();
    }

    @Override
    public Optional<Permission> getPermissionById(String id) {
        List<Permission> permissions =
                repository.getPermissions(Map.ofEntries(entry("_id", id)), "", 0, 0, "").get();
        if (permissions.size() != 0)
            return Optional.of(permissions.get(0));
        return Optional.empty();
    }
}
