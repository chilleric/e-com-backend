package com.example.ecom.controller;

import com.example.ecom.constant.LanguageMessageKey;
import com.example.ecom.dto.common.CommonResponse;
import com.example.ecom.dto.common.ListWrapperResponse;
import com.example.ecom.dto.common.ValidationResult;
import com.example.ecom.dto.message.ChatRoom;
import com.example.ecom.dto.message.MessageRequest;
import com.example.ecom.dto.message.MessageResponse;
import com.example.ecom.dto.message.OnlineUserResponse;
import com.example.ecom.service.message.MessageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/message")
public class MessageController extends AbstractController<MessageService> {

  @SecurityRequirement(name = "Bearer Authentication")
  @PostMapping("/to-chat-room")
  public ResponseEntity<CommonResponse<String>> toChatRoom(HttpServletRequest request) {
    ValidationResult result = validateToken(request, false);
    service.addOnlineUser(result.getLoginId());
    return new ResponseEntity<CommonResponse<String>>(
        new CommonResponse<String>(true, null, LanguageMessageKey.CONNECTED_TO_CHAT,
            HttpStatus.OK.value(), new ArrayList<>()),
        null,
        HttpStatus.OK.value());
  }

  @SecurityRequirement(name = "Bearer Authentication")
  @PostMapping("/out-chat-room")
  public ResponseEntity<CommonResponse<String>> outChatRoom(HttpServletRequest request) {
    ValidationResult result = validateToken(request, false);
    service.removeOnlineUser(result.getLoginId());
    return new ResponseEntity<CommonResponse<String>>(
        new CommonResponse<String>(true, null, LanguageMessageKey.DISCONNECT_CHAT,
            HttpStatus.OK.value(), new ArrayList<>()),
        null,
        HttpStatus.OK.value());
  }

  @SecurityRequirement(name = "Bearer Authentication")
  @PostMapping("/send-message")
  public ResponseEntity<CommonResponse<MessageResponse>> sendMessages(
      @RequestBody MessageRequest messageRequest,
      @RequestParam("id") String receiveId,
      HttpServletRequest request) {
    ValidationResult result = validateToken(request, false);
    return response(service.sendMessage(messageRequest, result.getLoginId(), receiveId),
        LanguageMessageKey.SUCCESS, new ArrayList<>());

  }

  @GetMapping("/online-users")
  public Flux<ServerSentEvent<List<OnlineUserResponse>>> streamUsers(@RequestParam String token) {
    ValidationResult result = validateSSE(token);
    return service.getOnlineUsers(result.getLoginId());
  }

  @GetMapping("/get-last-messages")
  public Flux<ServerSentEvent<MessageResponse>> streamLastMessage(@RequestParam String token) {
    ValidationResult result = validateSSE(token);
    return service.getLastUserMessage(result.getLoginId());
  }

  @SecurityRequirement(name = "Bearer Authentication")
  @GetMapping("/get-old-messages")
  public ResponseEntity<CommonResponse<ListWrapperResponse<MessageResponse>>> getOldSendMessage(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam("id") String sendId, HttpServletRequest request) {
    ValidationResult result = validateToken(request, false);
    return response(service.getOldMessage(result.getLoginId(), sendId, page),
        LanguageMessageKey.SUCCESS, new ArrayList<>());
  }

  @SecurityRequirement(name = "Bearer Authentication")
  @GetMapping(value = "/get-chat-room")
  public ResponseEntity<CommonResponse<List<ChatRoom>>> getChatRoom(
      @RequestParam(defaultValue = "1") int page,
      HttpServletRequest request) {
    ValidationResult result = validateToken(request, false);
    return response(service.getChatroom(result.getLoginId(), page), LanguageMessageKey.SUCCESS,
        new ArrayList<>());
  }

}
