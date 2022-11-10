package com.example.ecom.service.message;

import java.util.List;
import java.util.Optional;

import org.springframework.http.codec.ServerSentEvent;

import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.message.ChatRoom;
import com.example.ecom.dto.message.MessageRequest;
import com.example.ecom.dto.message.MessageResponse;

import reactor.core.publisher.Flux;

public interface MessageService {

    Optional<ListWrapperResponse<MessageResponse>> getReceiveMessages(String userId, String sendId, int page);

    Optional<ListWrapperResponse<MessageResponse>> getSendMessages(String userId, String sendId, int page);

    Optional<List<ChatRoom>> getChatroom(String loginId);

    void sendMessage(MessageRequest messageRequest);

    void addOnlineUser(String userId);

    void removeOnlineUser(String userId);

    Flux<ServerSentEvent<List<String>>> getOnlineUsers();

    Flux<ServerSentEvent<MessageResponse>> getLastUserMessage(String userId);
}
