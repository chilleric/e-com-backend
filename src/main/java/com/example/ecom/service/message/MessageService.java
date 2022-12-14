package com.example.ecom.service.message;

import java.util.List;
import java.util.Optional;
import org.springframework.http.codec.ServerSentEvent;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.message.ChatRoom;
import com.example.ecom.dto.message.MessageRequest;
import com.example.ecom.dto.message.MessageResponse;
import com.example.ecom.dto.message.OnlineUserResponse;
import reactor.core.publisher.Flux;

public interface MessageService {

    Optional<ListWrapperResponse<MessageResponse>> getOldMessage(String userId, String sendId,
            int page);

    Optional<List<ChatRoom>> getChatroom(String loginId, int page);

    Optional<MessageResponse> sendMessage(MessageRequest messageRequest, String loginId,
            String receiveId);

    void addOnlineUser(String userId);

    void removeOnlineUser(String userId);

    Flux<ServerSentEvent<List<OnlineUserResponse>>> getOnlineUsers(String userId);

    Flux<ServerSentEvent<MessageResponse>> getLastUserMessage(String userId);
}
