package com.smartcampus.controller;

import com.smartcampus.dto.request.ChatRequest;
import com.smartcampus.dto.response.ApiResponse;
import com.smartcampus.dto.response.ChatResponse;
import com.smartcampus.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponse>> chat(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = chatService.processMessage(request.getMessage());
        return ResponseEntity.ok(ApiResponse.success("Chat response generated", response));
    }
}
