package com.reclaim.self.controller;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rag")
public class RagController {


    private final VectorStore vectorStore;

    public RagController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }


    @PostMapping("/importData")
    public String importData(@RequestParam("data") String data) {
        Document document = Document.builder()
                .text(data)
                .build();
        vectorStore.add(List.of(document));
        return "success";
    }


    @GetMapping("/search")
    public List<Document> search(@RequestParam(name = "query") String query) {
        SearchRequest request = SearchRequest.builder()
                .topK(10)
                .query(query)
//                .similarityThreshold(0.8)
                .build();
        return vectorStore.similaritySearch(request);
    }
}
