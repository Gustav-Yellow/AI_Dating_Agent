package com.ai.aiagent.app;

import com.ai.aiagent.advisor.MyLoggerAdvisor;
import com.ai.aiagent.chatmemory.FileBasedChatMemory;
import com.ai.aiagent.rag.LoveAppRagCustomAdvisorFactory;
import com.ai.aiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，" +
            "告知用户可倾诉恋爱难题。围绕单身、恋爱、已婚三种状态提问：" +
            "单身状态询问社交圈拓展及追求心仪对象的困扰；恋爱状态询问沟通、" +
            "习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    /**
     * 初始化 ChatClient 客户端
     * @param dashscopeChatModel
     */
    public LoveApp(ChatModel dashscopeChatModel) {

        // 文件记忆保存路径
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        // 自定义的基于本地文件保存会话记忆
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);

        // 初始化基于内存的对话记忆
        // ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),  // 不指定具体的id的话就是所有对话都共享同一个id，默认轮次次数为100轮
                        // 自定义日志 Advisor，可按需开启
                        new MyLoggerAdvisor()
                        // 自定义 ReReadingAdvisor 提高推理能力，但是带来的弊端是输入的token会翻倍
//,                       new ReReadingAdvisor()
                )
                .build();
    }

    /**
     * AI 基础对话（支持多轮对话记忆，对话记忆保存在内存中）
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId) {
        // 通过 chatClient 调用ai大模型对话，此时再单独设置advisor中有关对话id和记忆轮数的设置
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)) // 这里设置的对话记忆的轮数为10轮
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    // 通过record，快速定义类变量
    record LoveReport(String title, List<String> suggestions) {
    }

    /**
     * AI 恋爱报告功能（实战结构化输出）
     * @param message
     * @param chatId
     * @return
     */
    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表") // 新的系统提示词定义了输出恋爱报告的结构
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }

    // AI 恋爱RAG知识库问答功能
    // Resource会自动注入
    @Resource
    private VectorStore loveAppVectorStore;

    // 基于阿里云知识库服务的RAG知识库问答功能
    @Resource
    private Advisor loveAppRagCloudAdvisor;

    // 基于PGVector的RAG知识库问答功能
    @Resource
    private VectorStore pgVectorVectorStore;

    // 查询重写
    @Resource
    private QueryRewriter queryRewriter;

    public String doChatWithRag(String message, String chatId) {
        // 查询重写
        String rewrittenMessage = queryRewriter.doQueryRewrite(message);

        ChatResponse chatResponse = chatClient
                .prompt()
                // 这里使用重写后的用户查询
                .user(rewrittenMessage)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                // 简单的情况是使用QuestionAnswerAdvisor，但是这个advisor会把所有向量库中的文档都查询一遍，所以这里使用自定义的advisor
                // 调用 loveAppVectorStore 类中实现的本地内存向量存储
                // .advisors(new QuestionAnswerAdvisor(loveAppVectorStore)
                // 应用 RAG 增强检索服务（阿里云知识库服务）
                // .advisors(loveAppRagCloudAdvisor)
                // 应用 RAG 检索增强服务（基于PGVector）
                // .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
                // 调用自定义的 RAG 检索增强的 Advisor，这样我们可以限定只查询的范围
                .advisors(LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(loveAppVectorStore, "单身"))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

}
