package com.reclaim.self.controller;


import com.reclaim.self.entity.MemoryMessage;
import com.reclaim.self.mapper.MemoryMessageMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/memoryMessage")
public class MemoryMessageController {


    private final MemoryMessageMapper memoryMessageMapper;

    public MemoryMessageController(MemoryMessageMapper memoryMessageMapper) {
        this.memoryMessageMapper = memoryMessageMapper;
    }


    @PostMapping("/add")
    public MemoryMessage add(@RequestBody MemoryMessage memoryMessage) {
        this.memoryMessageMapper.insert(memoryMessage);

        return this.memoryMessageMapper.selectById(memoryMessage.getId());
    }
}
