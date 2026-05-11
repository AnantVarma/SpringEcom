package com.anant.SpringEcom.controller;

import com.anant.SpringEcom.service.ChatBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/chat")
public class ChatBotController {
    @Autowired
    private ChatBotService chatBotService;
    @GetMapping("/ask")
    public ResponseEntity<String> chatBot(@RequestParam String message){
        String response = chatBotService.getBotRespnse(message);
        return ResponseEntity.ok(response);
    }
}
