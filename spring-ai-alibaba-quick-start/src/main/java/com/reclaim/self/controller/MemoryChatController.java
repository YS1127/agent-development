package com.reclaim.self.controller;


import com.reclaim.self.advisors.CustomChatMemory;
import com.reclaim.self.repository.CustomChatMemoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/memoryChat")
public class MemoryChatController {


    private final ChatClient chatClient;

    public MemoryChatController(ChatClient.Builder builder) {

        this.chatClient = builder.defaultAdvisors(chatMemoryAdvisor).build();
    }
}
