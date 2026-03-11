package com.ai.aiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Spring AI 框架调用 AI 大模型
 */
@Component
public class SpringAiInvoke implements CommandLineRunner {

    @Resource
    private ChatModel dashscopeChatModel;

    @Override
    public void run(String... args) {
        // 历史对话上下文拦截器
        // 注意该类型的记忆拦截器需要以来ChatMemory来指定信息存放的位置，并辅助查找对应的历史对话。
        // MessageChatMemoryAdvisor messageChatMemoryAdvisor = new MessageChatMemoryAdvisor();
        AssistantMessage assistantMessage = dashscopeChatModel.call(new Prompt("你好，我是齐裂！"))
                .getResult()
                .getOutput();
        System.out.println(assistantMessage.getText());

    }
}
