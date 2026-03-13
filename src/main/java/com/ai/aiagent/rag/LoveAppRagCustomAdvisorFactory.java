package com.ai.aiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * 创建自定义的 RAG 检索增强 Advisor 工厂
 */
@Slf4j
public class LoveAppRagCustomAdvisorFactory {

    /**
     * 创建自定义的 RAG 检索增强 Advisor
     * @param vectorStore
     * @param status
     * @return
     */
    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore, String status) {
        // 这里是根据元信息过滤文档，只检索所提供的 status 的文档
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status)
                .build();

        // 这里是构建基于向量数据库的文档检索器
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore) // 这里是注入向量数据库
                .filterExpression(expression) // 这里是根据元信息过滤文档，只检索给定 status 标签的文档
                .similarityThreshold(0.5) // 这里是设置相似度阈值，只检索相似度大于 0.5 的文档
                .topK(3) // 这里是设置返回的文档数量，最多返回 3 个文档
                .build();

        // 返回检索增强 Advisor
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createInstance()) // 这里是注入自定义的上下文查询增强器
                .build();

    }

}
