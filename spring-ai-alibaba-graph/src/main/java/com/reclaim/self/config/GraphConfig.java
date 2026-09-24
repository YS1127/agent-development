package com.reclaim.self.config;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.reclaim.self.node.SentenceConstructionNode;
import com.reclaim.self.node.TranslationNode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GraphConfig {

    private static final Logger log = LoggerFactory.getLogger(GraphConfig.class);


    @Bean("quickStartGraph")
    public CompiledGraph quickStartGraph(ChatClient.Builder builder) throws GraphStateException {
        KeyStrategyFactory stateFactory = () -> Map.of("input1", new ReplaceStrategy(), "input2", new ReplaceStrategy());
        //-- 定义状态图
        StateGraph stateGraph = new StateGraph("quickStartGraph", stateFactory);

        //-- 定义节点
        stateGraph.addNode("node1", AsyncNodeAction.node_async(t -> {
            log.info("state:{}", stateGraph);
            return Map.of("input1", 1, "input2", 1);
        }));

        stateGraph.addNode("node2", AsyncNodeAction.node_async(t -> {
            log.info("state:{}", stateGraph);
            return Map.of("input1", 2, "input2", 2);
        }));

        //-- 定义边
        stateGraph.addEdge(StateGraph.START, "node1");
        stateGraph.addEdge("node1", "node2");
        stateGraph.addEdge("node2", StateGraph.END);

        // 编译状态图
        return stateGraph.compile();


    }


    @Bean("simpleGraph")
    public CompiledGraph simpleGraph(ChatClient.Builder builder) throws GraphStateException {
        KeyStrategyFactory keyStrategyFactory = () -> Map.of("word", new ReplaceStrategy());

        // 创建状态图
        StateGraph stateGraph = new StateGraph("simpleGraph", keyStrategyFactory);
        // 添加节点
        stateGraph.addNode("sentenceConstructionNode",
                AsyncNodeAction.node_async(new SentenceConstructionNode(builder)));
        stateGraph.addNode("translationNode",
                AsyncNodeAction.node_async(new TranslationNode(builder)));
        // 定义边
        stateGraph.addEdge(StateGraph.START,"sentenceConstructionNode");
        stateGraph.addEdge("sentenceConstructionNode","translationNode");
        stateGraph.addEdge("translationNode",StateGraph.END);
        // 编译状态图 放入容器
        return stateGraph.compile();

    }
}
