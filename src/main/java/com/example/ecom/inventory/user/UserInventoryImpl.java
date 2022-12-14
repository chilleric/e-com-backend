package com.example.ecom.inventory.user;

import static java.util.Map.entry;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.example.ecom.inventory.AbstractInventory;
import com.example.ecom.repository.user.User;
import com.example.ecom.repository.user.UserRepository;

@Service
public class UserInventoryImpl extends AbstractInventory<UserRepository> implements UserInventory {

  @Override
  public Optional<User> findUserById(String userId) {
    List<User> users = repository.getUsers(Map.ofEntries(entry("_id", userId)), "", 0, 0, "").get();
    if (users.size() != 0) {
      return Optional.of(users.get(0));
    }
    return Optional.empty();
  }

  @Override
  public Optional<User> getActiveUserById(String userId) {
    List<User> users = repository
        .getUsers(Map.ofEntries(entry("_id", userId), entry("deleted", "0")), "", 0, 0, "").get();
    if (users.size() != 0) {
      return Optional.of(users.get(0));
    }
    return Optional.empty();
  }

  @Override
  public Optional<User> findUserByEmail(String email) {
    List<User> users =
        repository.getUsers(Map.ofEntries(entry("email", email)), "", 0, 0, "").get();
    if (users.size() != 0) {
      return Optional.of(users.get(0));
    }
    return Optional.empty();
  }

  @Override
  public Optional<User> findUserByPhone(String phone) {
    List<User> users =
        repository.getUsers(Map.ofEntries(entry("phone", phone)), "", 0, 0, "").get();
    if (users.size() != 0) {
      return Optional.of(users.get(0));
    }
    return Optional.empty();
  }

  @Override
  public Optional<User> findUserByUsername(String username) {
    List<User> users =
        repository.getUsers(Map.ofEntries(entry("username", username)), "", 0, 0, "").get();
    if (users.size() != 0) {
      return Optional.of(users.get(0));
    }
    return Optional.empty();
  }
}
