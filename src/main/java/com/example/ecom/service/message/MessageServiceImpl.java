package com.example.ecom.service.message;

import static java.util.Map.entry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.message.ChatRoom;
import com.example.ecom.dto.message.MessageRequest;
import com.example.ecom.dto.message.MessageResponse;
import com.example.ecom.dto.message.OnlineUserResponse;
import com.example.ecom.exception.ResourceNotFoundException;
import com.example.ecom.repository.message.Message;
import com.example.ecom.repository.message.MessageRepository;
import com.example.ecom.repository.user.User;
import com.example.ecom.repository.user.UserRepository;
import com.example.ecom.service.AbstractService;
import com.example.ecom.utils.DateFormat;

import reactor.core.publisher.Flux;

@Service
public class MessageServiceImpl extends AbstractService<MessageRepository> implements MessageService {

        @Autowired
        private UserRepository userRepository;

        private List<OnlineUserResponse> onlineUsers = new ArrayList<>();

        private List<MessageResponse> messages = new ArrayList<>();

        @Override
        public Optional<ListWrapperResponse<MessageResponse>> getOldMessage(String userId, String sendId, int page) {
                List<Message> result = new ArrayList<>();
                repository.getMessage(
                                Map.ofEntries(entry("sendId", userId), entry("receiveId", sendId)), "DESC", page, 5,
                                "create").get().forEach(message -> {
                                        result.add(message);
                                });
                repository.getMessage(
                                Map.ofEntries(entry("sendId", sendId), entry("receiveId", userId)), "DESC", page, 5,
                                "create").get().forEach(message -> {
                                        result.add(message);
                                });
                return Optional.of(new ListWrapperResponse<MessageResponse>(
                                result.stream()
                                                .map(sendMessage -> new MessageResponse(sendMessage.get_id().toString(),
                                                                sendMessage.getSendId().toString(),
                                                                sendMessage.getReceiveId().toString(),
                                                                sendMessage.getContext(),
                                                                sendMessage.getCreate()))
                                                .collect(Collectors.toList()),
                                page, 0, 0));
        }

        @Override
        public Optional<List<ChatRoom>> getChatroom(String loginId, int page) {
                List<ChatRoom> result = new ArrayList<>();
                repository.getMessage(Map.ofEntries(entry("sendId", loginId)), "", page, 10, "")
                                .get().forEach(message -> {
                                        List<User> users = userRepository.getUsers(
                                                        Map.ofEntries(entry("_id", message.getReceiveId().toString())),
                                                        "", 0, 0, "").get();
                                        boolean hasId = false;
                                        for (int i = 0; i < result.size(); i++) {
                                                if (result.get(i).getReceiveId()
                                                                .compareTo(message.getReceiveId().toString()) == 0) {
                                                        hasId = true;
                                                        break;
                                                }
                                        }
                                        if (users.size() != 0 && !hasId) {
                                                result.add(new ChatRoom(users.get(0).get_id().toString(),
                                                                users.get(0).getFirstName() + " "
                                                                                + users.get(0).getLastName()));
                                        }
                                });
                return Optional.of(result);
        }

        @Override
        public void addOnlineUser(String userId) {
                List<User> users = userRepository.getUsers(Map.ofEntries(entry("_id", userId)), "", 0, 0, "").get();
                if (users.size() == 0) {
                        throw new ResourceNotFoundException("Not found user!");
                }
                if (onlineUsers.size() == 0) {
                        onlineUsers.add(new OnlineUserResponse(
                                        users.get(0).getFirstName() + " " + users.get(0).getLastName(), userId));
                } else {
                        boolean isOnline = false;
                        for (OnlineUserResponse user : onlineUsers) {
                                if (user.getId().compareTo(userId) == 0) {
                                        isOnline = true;
                                }
                        }
                        if (isOnline != true) {
                                onlineUsers.add(new OnlineUserResponse(
                                                users.get(0).getFirstName() + " " + users.get(0).getLastName(),
                                                userId));
                        }
                }
        }

        @Override
        public void removeOnlineUser(String userId) {
                List<User> users = userRepository.getUsers(Map.ofEntries(entry("_id", userId)), "", 0, 0, "").get();
                if (users.size() == 0) {
                        throw new ResourceNotFoundException("Not found user!");
                }
                if (onlineUsers.size() != 0) {
                        boolean isOnline = false;
                        int deleteIndex = 0;
                        for (int i = 0; i < onlineUsers.size(); i++) {
                                if (onlineUsers.get(i).getId().compareTo(userId) == 0) {
                                        deleteIndex = i;
                                        isOnline = true;
                                }
                        }
                        if (isOnline == true) {
                                onlineUsers.remove(deleteIndex);
                        }
                }
        }

        @Override
        public Optional<MessageResponse> sendMessage(MessageRequest messageRequest, String loginId, String receiveId) {
                validate(messageRequest);
                ObjectId id = new ObjectId();
                Date now = DateFormat.getCurrentTime();
                Message message = new Message(id, new ObjectId(loginId),
                                new ObjectId(receiveId), messageRequest.getMessage(),
                                now);
                repository.insertAndUpdate(message);
                messages.add(new MessageResponse(id.toString(), loginId, receiveId, messageRequest.getMessage(),
                                now));
                return Optional.of(new MessageResponse(id.toString(), loginId, receiveId, messageRequest.getMessage(),
                                now));

        }

        @Override
        public Flux<ServerSentEvent<List<OnlineUserResponse>>> getOnlineUsers(String userId) {
                return Flux.interval(Duration.ofSeconds(1))
                                .map(sequence -> ServerSentEvent.<List<OnlineUserResponse>>builder()
                                                .id(String.valueOf(sequence)).event("get-online-users-event")
                                                .data(onlineUsers)
                                                .build());
        }

        public MessageResponse getLastMessage(String userId) {

                MessageResponse result = new MessageResponse("", "", "", "", null);

                if (messages.size() == 0)
                        return result;

                messages.forEach((mes) -> {
                        if (result.getCreated() == null) {
                                result.setId(mes.getId());
                                result.setSendId(mes.getSendId());
                                result.setReceiveId(mes.getReceiveId());
                                result.setContext(mes.getContext());
                                result.setCreated(mes.getCreated());
                        } else {
                                if (result.getCreated().compareTo(mes.getCreated()) < 0) {
                                        result.setId(mes.getId());
                                        result.setSendId(mes.getSendId());
                                        result.setReceiveId(mes.getReceiveId());
                                        result.setContext(mes.getContext());
                                        result.setCreated(mes.getCreated());
                                }
                        }
                });

                return result;
        }

        @Override
        public Flux<ServerSentEvent<MessageResponse>> getLastUserMessage(String userId) {
                return Flux.interval(Duration.ofSeconds(1))
                                .map(sequence -> ServerSentEvent.<MessageResponse>builder()
                                                .id(String.valueOf(sequence)).event("get-last-message")
                                                .data(getLastMessage(userId))
                                                .build());

        }
}
