package com.ai.aiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于 AI 的文档元信息增强器（为文档补充元信息（标签信息））
 */
@Component
public class MyKeywordEnricher {

    // 这里是注入一个基于 DashScope 的 ChatModel 模型，用于增强文档元信息，这里是由配置文件中的模型信息自动注入的
    @Resource
    private ChatModel dashScopeChatModel;

    public List<Document> enrichDocuments(List<Document> documents) {
        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(dashScopeChatModel, 5);
        return keywordMetadataEnricher.apply(documents);
    }

}
