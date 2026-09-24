package com.reclaim.self.controller;


import com.reclaim.self.tool.TimeTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/coffee")
@Slf4j
public class CoffeeRagController {


    private final VectorStore vectorStore;

    private final ChatClient chatClient;

    public CoffeeRagController(VectorStore vectorStore, ChatClient.Builder builder) {
        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .topK(3)
                .vectorStore(vectorStore)
                .build();
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .build();
        this.vectorStore = vectorStore;
        this.chatClient = builder
                .defaultAdvisors(retrievalAugmentationAdvisor)
                .defaultTools(new TimeTools())
                .build();
    }

    /**
     * 导入数据到向量数据库
     * 从classpath下的QA.csv文件读取问答数据并向量化存储
     *
     * @return 导入结果消息
     */
    @PostMapping("importData")
    public String importData() {
        try {
            // 读取Classpath下的QA.csv文件
            ClassPathResource resource = new ClassPathResource("QA.csv");
            InputStreamReader reader = new InputStreamReader(resource.getInputStream());

            // 使用Apache Commons CSV解析CSV文件
            CSVParser csvParser = CSVFormat.DEFAULT
                    .builder()
                    .setHeader() // 第一行作为标题
                    .setSkipHeaderRecord(true) // 跳过标题行
                    .build()
                    .parse(reader);

            List<Document> documents = new ArrayList<>();

            // 遍历每一行记录
            for (CSVRecord record : csvParser) {
                // 获取问题和回答字段
                String question = record.get("问题");
                String answer = record.get("回答");

                // 将问题和回答组合成文档内容
                String content = "问题: " + question + "\n回答: " + answer;

                // 创建Document对象
                Document document = new Document(content);

                // 添加到文档列表
                documents.add(document);
            }

            // 关闭解析器
            csvParser.close();

            // 将文档存入向量数据库
            vectorStore.add(documents);

            return "成功导入";
        } catch (IOException e) {
            log.error("导入数据时发生IO异常", e);
            return "导入失败: " + e.getMessage();
        }
    }


    @GetMapping("/rag-ask")
    public String ragAskQuestion(@RequestParam(name = "query") String query) {
        return this.chatClient.prompt()
                .system("你是一个咖啡店员工")
                .user(query)
                .call().content();
    }
}
