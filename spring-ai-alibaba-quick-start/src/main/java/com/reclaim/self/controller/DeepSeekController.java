package com.reclaim.self.controller;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/deepseek")
public class DeepSeekController {

    private final ChatModel chatModel;

    public DeepSeekController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/simple")
    public String simple(@RequestParam(name = "query") String query) {
        return chatModel.call(query);
    }


    @GetMapping("/message")
    public String message(@RequestParam(name = "query") String query) {
        SystemMessage systemMessage = new SystemMessage("你是一个资深白金玄幻小说作家");
        UserMessage userMessage = new UserMessage(query);
        return chatModel.call(systemMessage, userMessage);
    }

    @GetMapping("/prompt")
    public String prompt(@RequestParam(name = "query") String query) {
        SystemMessage systemMessage = new SystemMessage("你是一个资深白金玄幻小说作家");
        UserMessage userMessage = new UserMessage(query);
        ChatOptions chatOptions = ChatOptions.builder()
                .model("deepseek-v4-pro")
                .build();
        Prompt prompt = Prompt.builder()
                .messages(List.of(systemMessage, userMessage))
                .chatOptions(chatOptions)
                .build();
        ChatResponse chatResponse = chatModel.call(prompt);
        return chatResponse.getResult().getOutput().getText();
    }

    @GetMapping("/stream")
    public Flux<String> stream(@RequestParam(name = "query") String query) {
        SystemMessage systemMessage = new SystemMessage("你是一个资深白金玄幻小说作家");
        UserMessage userMessage = new UserMessage(query);
        return chatModel.stream(systemMessage, userMessage);
    }
}
