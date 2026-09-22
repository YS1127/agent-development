package com.reclaim.self.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reclaim.self.entity.MemoryMessage;
import com.reclaim.self.mapper.MemoryMessageMapper;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.*;

import java.util.Collections;
import java.util.List;

public class CustomChatMemoryRepository implements ChatMemoryRepository {

    private final MemoryMessageMapper memoryMessageMapper;

    private final ObjectMapper objectMapper;

    public CustomChatMemoryRepository(MemoryMessageMapper memoryMessageMapper, ObjectMapper objectMapper) {
        this.memoryMessageMapper = memoryMessageMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<String> findConversationIds() {
        // 只查 conversation_id 这一列，避免把大 JSON 全捞出来
        LambdaQueryWrapper<MemoryMessage> wrapper = Wrappers.<MemoryMessage>lambdaQuery()
                .select(MemoryMessage::getConversationId);
        return memoryMessageMapper.selectList(wrapper).stream()
                .map(MemoryMessage::getConversationId)
                .toList();
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        LambdaQueryWrapper<MemoryMessage> wrapper = Wrappers.<MemoryMessage>lambdaQuery()
                .eq(MemoryMessage::getConversationId, conversationId);
        MemoryMessage memoryMessage = memoryMessageMapper.selectOne(wrapper);
        if (memoryMessage == null || memoryMessage.getMessages() == null || memoryMessage.getMessages().isBlank()) {
            return Collections.emptyList();
        }
        return parseMessages(memoryMessage.getMessages());
    }

    /**
     * Replaces all the existing messages for the given conversation ID with the provided
     * messages.
     *
     * @param conversationId
     * @param messages
     */
    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        String json = writeMessages(messages);

        MemoryMessage existing = memoryMessageMapper.selectOne(
                Wrappers.<MemoryMessage>lambdaQuery()
                        .eq(MemoryMessage::getConversationId, conversationId));

        if (existing == null) {
            MemoryMessage entity = new MemoryMessage();
            entity.setConversationId(conversationId);
            entity.setMessages(json);
            memoryMessageMapper.insert(entity);
        } else {
            existing.setMessages(json);
            memoryMessageMapper.updateById(existing);
        }
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        memoryMessageMapper.delete(
                Wrappers.<MemoryMessage>lambdaQuery()
                        .eq(MemoryMessage::getConversationId, conversationId));
    }


    /** 对应 messages 列里每个元素的 JSON 结构 */
    private record MessageRecord(MessageType type, String text) {}

    /**
     * 把 messages 列里的 JSON 数组反序列化成 List<Message>
     */
    private List<Message> parseMessages(String json) {
        try {
            List<MessageRecord> records = objectMapper.readValue(
                    json, new TypeReference<List<MessageRecord>>() {});
            return records.stream()
                    .map(this::toMessage)
                    .filter(java.util.Objects::nonNull)
                    .toList();
        } catch (JsonProcessingException e) {
            // 日志记录，返回空列表避免整个会话读不出来
            return Collections.emptyList();
        }
    }

    private Message toMessage(MessageRecord record) {
        if (record == null || record.type() == null || record.text() == null) {
            return null;
        }
        return switch (record.type()) {
            case USER      -> new UserMessage(record.text());
            case ASSISTANT -> new AssistantMessage(record.text());
            case SYSTEM    -> new SystemMessage(record.text());
            default        -> null;   // TOOL 等按需扩展
        };
    }

    private String writeMessages(List<Message> messages) {
        List<MessageRecord> records = messages.stream()
                .map(m -> new MessageRecord(m.getMessageType(), m.getText()))
                .toList();
        try {
            return objectMapper.writeValueAsString(records);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化消息失败", e);
        }
    }
}
