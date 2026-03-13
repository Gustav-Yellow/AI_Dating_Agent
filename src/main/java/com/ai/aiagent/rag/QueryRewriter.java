package com.ai.aiagent.rag;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

/**
 * 查询重写器
 */
@Component
public class QueryRewriter {

    private final QueryTransformer queryTrandformer;

    public QueryRewriter(ChatModel dashScopeChatModel) {
        ChatClient.Builder chatClientBuilder = ChatClient.builder(dashScopeChatModel);
        // 这里是注入一个基于 DashScope 的 ChatModel 模型，用于重写查询，这里是由配置文件中的模型信息自动注入的
        queryTrandformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
    }

    /**
     * 执行查询重写
     * @param prompt
     * @return
     */
    public String doQueryRewrite(String prompt) {
        Query query = new Query(prompt);
        // 执行查询重写
        Query rewrittenQuery = queryTrandformer.transform(query);
        // 输出重写后的查询
        return rewrittenQuery.text();
    }

}
