package com.example.ecom.inventory.user;

import com.example.ecom.repository.user.User;

import java.util.Optional;

public interface UserInventory {
    Optional<User> findUserById(String userId);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByPhone(String phone);

    Optional<User> findUserByUsername(String username);
}
