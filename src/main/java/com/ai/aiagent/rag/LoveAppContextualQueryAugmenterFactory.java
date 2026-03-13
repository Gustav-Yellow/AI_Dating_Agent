package com.ai.aiagent.rag;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * 恋爱大师上下文查询增强器工厂
 */
@Slf4j
public class LoveAppContextualQueryAugmenterFactory {

    public static ContextualQueryAugmenter createInstance() {
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
                你应该输出下面的内容：
                抱歉，我只能回答恋爱相关的问题，别的没办法帮到您哦，
                有问题可以联系月老解决这件事。
                """);
        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(false) // 不允许上下文为空
                .emptyContextPromptTemplate(emptyContextPromptTemplate) // 当上下文为空时，使用自定义的PromptTemplate
                .build();
    }

}
