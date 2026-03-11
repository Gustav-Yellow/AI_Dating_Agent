package com.ai.aiagent.demo.invoke;

import java.util.Arrays;
import java.util.Collections;

import com.ai.aiagent.utils.TestApiKey;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.alibaba.dashscope.utils.Constants;

/**
 * 直接通过 sdk 的方式调用大模型接口
 */
public class SdkAiInvoke {

    static {Constants.baseHttpApiUrl="https://dashscope.aliyuncs.com/api/v1";}

    public static void simpleMultiModalConversationCall()
            throws ApiException, NoApiKeyException, UploadFileException {
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(
                        Collections.singletonMap("image", "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20241022/emyrja/dog_and_girl.jpeg"),
                        Collections.singletonMap("text", "图中描绘的是什么景象?"))).build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .apiKey(TestApiKey.API_KEY)
                .model("qwen3.5-flash")
                .messages(Arrays.asList(userMessage))
                .build();
        MultiModalConversationResult result = conv.call(param);
//        只打印content中回答的内容
//        System.out.println(result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text"));
        // 打印完整的 JSON 响应
        System.out.println(result);
    }
    public static void main(String[] args) {
        try {
            simpleMultiModalConversationCall();
        } catch (ApiException | NoApiKeyException | UploadFileException e) {
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }
}