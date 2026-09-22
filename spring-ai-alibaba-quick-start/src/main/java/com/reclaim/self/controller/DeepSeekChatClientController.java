package com.reclaim.self.controller;

import com.reclaim.self.entity.Book;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chatClient")
public class DeepSeekChatClientController {


    private final ChatClient chatClient;

    public DeepSeekChatClientController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }


    @GetMapping("/generationBook")
    public Book generationBook() {
        return this.chatClient.prompt()
                .user("请随机生成一本书，要求书名跟作者名都是中文")
                .call()
                .entity(Book.class);
    }

    @GetMapping("/stream")
    public Flux<String> stream() {
        return this.chatClient.prompt()
                .user("请随机生成一本书，要求书名跟作者名都是中文")
                .stream()
                .content();
    }
}
