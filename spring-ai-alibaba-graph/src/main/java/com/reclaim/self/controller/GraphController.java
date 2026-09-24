package com.reclaim.self.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/graph")
public class GraphController {

    private static final Logger log = LoggerFactory.getLogger(GraphController.class);
    private final CompiledGraph compiledGraph;
    private final CompiledGraph simpleGraph;

    public GraphController(@Qualifier("quickStartGraph") CompiledGraph compiledGraph, @Qualifier("simpleGraph") CompiledGraph simpleGraph) {
        this.compiledGraph = compiledGraph;
        this.simpleGraph = simpleGraph;
    }


    @GetMapping("/quickStartGraph")
    public String quickStartGraph() {
        Optional<OverAllState> overAllStateOptional = compiledGraph.call(Map.of());
        log.info("o: {}", overAllStateOptional);
        return "ok";
    }


    @GetMapping("simpleGraph")
    public Map<String, Object> simpleGraph(@RequestParam(name = "word") String word) {
        Optional<OverAllState> overAllStateOptional = simpleGraph.call(Map.of("word", word));
        Map<String, Object> data = overAllStateOptional.map(OverAllState::data).orElse(Map.of());
        return data;
    }
}
