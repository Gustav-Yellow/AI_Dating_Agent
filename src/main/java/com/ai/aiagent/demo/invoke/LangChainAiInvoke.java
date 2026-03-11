package com.ai.aiagent.demo.invoke;


import com.ai.aiagent.utils.TestApiKey;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;

public class LangChainAiInvoke {

    public static void main(String[] args) {
        ChatLanguageModel qwenChatModel = QwenChatModel.builder()
                .apiKey(TestApiKey.API_KEY)
                .modelName("qwen-flash")
                .build();
        // Langchain 在请求ai之后直接返回的就是完整的回答content而非json
        String answer = qwenChatModel.chat("我是裂！一个非常中二的初中少年。");
        System.out.println(answer);
    }

}
