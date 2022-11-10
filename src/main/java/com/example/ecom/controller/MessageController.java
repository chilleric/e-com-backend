package com.example.ecom.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.message.MessageRequest;
import com.example.ecom.dto.message.MessageResponse;
import com.example.ecom.service.message.MessageService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/message")
public class MessageController extends AbstractController<MessageService> {
    @PostMapping("/to-chat-room")
    public ResponseEntity<CommonResponse<String>> toChatRoom(@RequestParam("id") String userId) {
        service.addOnlineUser(userId);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, "Connected to Chat!",
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }

    @PostMapping("/out-chat-room")
    public ResponseEntity<CommonResponse<String>> outChatRoom(@RequestParam("id") String userId) {
        service.removeOnlineUser(userId);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, "Disconnected to Chat!",
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }

    @PostMapping("/send-message")
    public ResponseEntity<CommonResponse<String>> sendMessages(@RequestBody MessageRequest messageRequest) {
        service.sendMessage(messageRequest);
        return new ResponseEntity<CommonResponse<String>>(
                new CommonResponse<String>(true, null, "Message is sent!",
                        HttpStatus.OK.value()),
                null,
                HttpStatus.OK.value());
    }

    @GetMapping("/online-users")
    public Flux<ServerSentEvent<List<String>>> streamUsers() {
        return service.getOnlineUsers();
    }

    @GetMapping("/get-last-messages")
    public Flux<ServerSentEvent<MessageResponse>> streamLastMessage(@RequestParam("id") String userId) {
        return service.getLastUserMessage(userId);
    }

    @GetMapping("/get-old-message")
    public ResponseEntity<CommonResponse<ListWrapperResponse<MessageResponse>>> getOldSendMessage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam String userId,
            @RequestParam String sendId) {
        return response(service.getSendMessages(userId, sendId, page), "get old sent message successfully!");
    }

    @GetMapping("/get-receive-message")
    public ResponseEntity<CommonResponse<ListWrapperResponse<MessageResponse>>> getOldReceiveMessage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam String userId,
            @RequestParam String sendId) {
        return response(service.getReceiveMessages(userId, sendId, page), "get old received message successfully!");
    }
}
