package com.example.ecom.inventory.user;

import java.util.Optional;
import com.example.ecom.repository.user.User;

public interface UserInventory {

  Optional<User> findUserById(String userId);

  Optional<User> getActiveUserById(String userId);

  Optional<User> findUserByEmail(String email);

  Optional<User> findUserByPhone(String phone);

  Optional<User> findUserByUsername(String username);
}
