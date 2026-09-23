package com.reclaim.self.controller;

import com.reclaim.self.advisors.CustomChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/memoryChat")
public class MemoryChatController {

    private final ChatClient chatClient;

    public MemoryChatController(ChatClient.Builder builder, CustomChatMemory customChatMemory) {
        MessageChatMemoryAdvisor chatMemoryAdvisor = MessageChatMemoryAdvisor
                .builder(customChatMemory)
                .build();

        this.chatClient = builder
                .defaultAdvisors(chatMemoryAdvisor)
                .build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(name = "conversationId") String conversationId, @RequestParam(name = "message") String message) {
        return this.chatClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}
