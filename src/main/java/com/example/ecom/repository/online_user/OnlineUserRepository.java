package com.example.ecom.repository.online_user;

import java.util.List;
import java.util.Optional;

public interface OnlineUserRepository {
    Optional<List<OnlineUser>> getOnlineUsers();

    void addOnlineUser(String UserId, String name);

    void removeOnlineUser(String userId);
}
