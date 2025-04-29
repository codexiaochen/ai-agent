package com.chen.aiagent.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ollamaController {

    @Resource
    private ChatModel deepseekR1ChatModel;

    @PostMapping("/ollama")
    public String getMessage(String message){
        ChatResponse response = deepseekR1ChatModel.call(new Prompt(message));
        String result = response.getResult().getOutput().getText();
        return result;
    }
}
