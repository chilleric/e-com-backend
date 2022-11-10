package com.example.ecom.service.message;

import static java.util.Map.entry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import com.example.ecom.constant.DateTime;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.message.ChatRoom;
import com.example.ecom.dto.message.MessageRequest;
import com.example.ecom.dto.message.MessageResponse;
import com.example.ecom.exception.ResourceNotFoundException;
import com.example.ecom.repository.message.Message;
import com.example.ecom.repository.message.MessageRepository;
import com.example.ecom.repository.online_user.OnlineUser;
import com.example.ecom.repository.online_user.OnlineUserRepository;
import com.example.ecom.repository.user.User;
import com.example.ecom.repository.user.UserRepository;
import com.example.ecom.service.AbstractService;
import com.example.ecom.utils.DateFormat;

import reactor.core.publisher.Flux;

@Service
public class MessageServiceImpl extends AbstractService<MessageRepository> implements MessageService {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private OnlineUserRepository onlineUserRepository;

        @Override
        public Optional<ListWrapperResponse<MessageResponse>> getReceiveMessages(String userId, String sendId,
                        int page) {
                List<Message> receiveMessages = repository.getMessage(
                                Map.ofEntries(entry("sendId", sendId), entry("receiveId", userId)), "ASC", page, 5,
                                "created").get();

                return Optional.of(new ListWrapperResponse<MessageResponse>(
                                receiveMessages.stream()
                                                .map(receiveMessage -> new MessageResponse(
                                                                receiveMessage.get_id().toString(),
                                                                receiveMessage.getSendId().toString(),
                                                                receiveMessage.getReceiveId().toString(),
                                                                receiveMessage.getContext(),
                                                                DateFormat.toDateString(
                                                                                receiveMessage.getCreate(),
                                                                                DateTime.YYYY_MM_DD)))
                                                .collect(Collectors.toList()),
                                page, 0, 0));
        }

        @Override
        public Optional<ListWrapperResponse<MessageResponse>> getSendMessages(String userId, String sendId, int page) {
                List<Message> sendMessages = repository.getMessage(
                                Map.ofEntries(entry("sendId", userId), entry("receiveId", sendId)), "ASC", page, 5,
                                "created").get();
                return Optional.of(new ListWrapperResponse<MessageResponse>(
                                sendMessages.stream()
                                                .map(sendMessage -> new MessageResponse(sendMessage.get_id().toString(),
                                                                sendMessage.getSendId().toString(),
                                                                sendMessage.getReceiveId().toString(),
                                                                sendMessage.getContext(),
                                                                DateFormat.toDateString(sendMessage.getCreate(),
                                                                                DateTime.YYYY_MM_DD)))
                                                .collect(Collectors.toList()),
                                page, 0, 0));
        }

        @Override
        public Optional<List<ChatRoom>> getChatroom(String loginId) {
                List<ChatRoom> result = new ArrayList<>();
                List<Message> getMessages = repository.getMessage(Map.ofEntries(entry("sendId", loginId)), "", 0, 0, "")
                                .get();
                getMessages.forEach(message -> {
                        List<User> users = userRepository.getUsers(null, "", 0, 0, "").get();
                        if (users.size() != 0) {
                                result.add(new ChatRoom(users.get(0).get_id().toString(),
                                                users.get(0).getFirstName() + " " + users.get(0).getLastName()));
                        }
                });
                for (int i = 0; i < result.size(); i++) {
                        for (int j = i; j < result.size(); j++) {
                                if (result.get(i).getReceiveId().compareTo(result.get(j).getReceiveId()) == 0) {
                                        result.remove(j);
                                }
                        }
                }
                return Optional.of(result);
        }

        @Override
        public void addOnlineUser(String userId) {
                List<OnlineUser> onlineUsers = onlineUserRepository.getOnlineUsers().get();
                List<User> users = userRepository.getUsers(Map.ofEntries(entry("_id", userId)), "", 0, 0, "").get();
                if (users.size() == 0) {
                        throw new ResourceNotFoundException("Not found user!");
                }
                if (onlineUsers.size() == 0) {
                        onlineUserRepository.addOnlineUser(userId,
                                        users.get(0).getFirstName() + " " + users.get(0).getLastName());
                } else {
                        boolean isOnline = false;
                        for (OnlineUser user : onlineUsers) {
                                if (user.getUserId().toString().compareTo(userId) == 0) {
                                        isOnline = true;
                                }
                        }
                        if (isOnline != true) {
                                onlineUserRepository.addOnlineUser(userId,
                                                users.get(0).getFirstName() + " " + users.get(0).getLastName());
                        }
                }
        }

        @Override
        public void removeOnlineUser(String userId) {
                List<OnlineUser> onlineUsers = onlineUserRepository.getOnlineUsers().get();
                List<User> users = userRepository.getUsers(Map.ofEntries(entry("_id", userId)), "", 0, 0, "").get();
                if (users.size() == 0) {
                        throw new ResourceNotFoundException("Not found user!");
                }
                if (onlineUsers.size() != 0) {
                        boolean isOnline = false;
                        for (OnlineUser user : onlineUsers) {
                                if (user.getUserId().toString().compareTo(userId) == 0) {
                                        isOnline = true;
                                }
                        }
                        if (isOnline == true) {
                                onlineUserRepository.removeOnlineUser(userId);
                        }
                }
        }

        @Override
        public void sendMessage(MessageRequest messageRequest) {
                Message message = new Message(null, new ObjectId(messageRequest.getSendId()),
                                new ObjectId(messageRequest.getReceiveId()), messageRequest.getMessage(),
                                DateFormat.getCurrentTime());
                repository.insertAndUpdate(message);

        }

        @Override
        public Flux<ServerSentEvent<List<String>>> getOnlineUsers() {
                List<OnlineUser> onlineUsers = onlineUserRepository.getOnlineUsers().get();
                return Flux.interval(Duration.ofSeconds(1)).map(sequence -> ServerSentEvent.<List<String>>builder()
                                .id(String.valueOf(sequence)).event("get-online-users-event").data(onlineUsers.stream()
                                                .map(user -> user.getUserId().toString()).collect(Collectors.toList()))
                                .build());
        }

        @Override
        public Flux<ServerSentEvent<MessageResponse>> getLastUserMessage(String userId) {
                List<Message> receiveMessages = repository.getMessage(
                                Map.ofEntries(entry("receiveId", userId)), "DESC", 0, 0,
                                "created").get();
                if (receiveMessages.size() == 0) {
                        return Flux.interval(Duration.ofSeconds(1))
                                        .map(sequence -> ServerSentEvent.<MessageResponse>builder()
                                                        .id(String.valueOf(sequence)).event("get-last-message")
                                                        .data(new MessageResponse("", "", "", "", ""))
                                                        .build());

                } else {
                        return Flux.interval(Duration.ofSeconds(1)).map(sequence -> ServerSentEvent
                                        .<MessageResponse>builder()
                                        .id(String.valueOf(sequence)).event("get-last-message")
                                        .data(new MessageResponse(receiveMessages.get(0).get_id().toString(),
                                                        receiveMessages.get(0).getSendId().toString(),
                                                        receiveMessages.get(0).getReceiveId().toString(),
                                                        receiveMessages.get(0).getContext(),
                                                        DateFormat.toDateString(receiveMessages.get(0).getCreate(),
                                                                        DateTime.YYYY_MM_DD)))
                                        .build());
                }

        }
}
