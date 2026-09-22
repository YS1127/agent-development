package com.reclaim.self.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("memory_message")
public class MemoryMessage {

    private Integer id;

    private String conversationId;

    private String messages;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;
}
