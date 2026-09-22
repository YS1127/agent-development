package com.reclaim.self.advisors;

import com.reclaim.self.repository.CustomChatMemoryRepository;
import lombok.Builder;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.context.annotation.Bean;

import java.util.List;

@Builder
public class CustomChatMemory implements ChatMemory {

    private final CustomChatMemoryRepository customChatMemoryRepository;

    public CustomChatMemory(CustomChatMemoryRepository customChatMemoryRepository) {
        this.customChatMemoryRepository = customChatMemoryRepository;
    }

    /**
     * Save the specified messages in the chat memory for the specified conversation.
     *
     * @param conversationId
     * @param messages
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        this.customChatMemoryRepository.saveAll(conversationId, messages);
    }

    /**
     * Get the messages in the chat memory for the specified conversation.
     *
     * @param conversationId
     */
    @Override
    public List<Message> get(String conversationId) {
        return this.customChatMemoryRepository.findByConversationId(conversationId);
    }

    /**
     * Clear the chat memory for the specified conversation.
     *
     * @param conversationId
     */
    @Override
    public void clear(String conversationId) {
        this.customChatMemoryRepository.deleteByConversationId(conversationId);
    }
}
