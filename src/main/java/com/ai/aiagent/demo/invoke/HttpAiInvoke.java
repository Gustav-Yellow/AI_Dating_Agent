package com.ai.aiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ai.aiagent.utils.TestApiKey;

import java.util.HashMap;
import java.util.Map;

public class HttpAiInvoke {

    // 修正后的 API 地址 (以 qwen3.5-flash 模型为例)
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

    public static void main(String[] args) {
        // 请替换为你的实际 API Key
        String apiKey = TestApiKey.API_KEY;

        // 构建请求头
        Map<String, String> headers = buildHeaders(apiKey);

        // 构建请求体
        JSONObject requestBody = buildRequestBody();

        // 发送请求并处理响应
        sendRequest(headers, requestBody);
    }

    /**
     * 构建请求头
     */
    private static Map<String, String> buildHeaders(String apiKey) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        headers.put("Content-Type", "application/json");
        return headers;
    }

    /**
     * 构建请求体 (严格按照 DashScope 的 API 文档格式)
     */
    private static JSONObject buildRequestBody() {
        JSONObject requestBody = new JSONObject();

        // 1. 指定模型
        requestBody.put("model", "qwen3.5-plus");

        // 2. 构建消息列表 (遵循对话历史格式)
        JSONArray messages = new JSONArray();

        // 可以添加 system 消息 (可选)
        JSONObject systemMsg = new JSONObject();
        systemMsg.set("role", "system");
        systemMsg.set("content", "You are a helpful assistant.");
        messages.add(systemMsg);

        // User 的提问
        JSONObject userMsg = new JSONObject();
        userMsg.set("role", "user");
        userMsg.set("content", "请写一首关于春天的诗。");
        messages.add(userMsg);

        // 将消息列表放入 input 字段
        JSONObject input = new JSONObject();
        input.put("messages", messages);
        requestBody.put("input", input);

        // 3. 参数配置 (非流式)
        JSONObject parameters = new JSONObject();
        parameters.put("result_format", "message"); // 推荐使用 message 格式
        // 注意：这里不设置 incremental_output，即为普通同步调用
        requestBody.put("parameters", parameters);

        return requestBody;
    }

    /**
     * 发送 HTTP 请求
     */
    private static void sendRequest(Map<String, String> headers, JSONObject requestBody) {
        HttpResponse response = null;
        try {
            response = HttpRequest.post(API_URL)
                    .addHeaders(headers)
                    .body(requestBody.toString())
                    .execute();

            // 处理响应
            if (response.isOk()) {
                String responseBody = response.body();
                System.out.println("=== API 响应结果 ===");
                System.out.println(responseBody);

                // 解析并打印 Token 消耗 (演示如何提取数据)
                JSONObject jsonResponse = new JSONObject(responseBody);
                JSONObject usage = jsonResponse.getJSONObject("usage");
                System.out.println("\n=== Token 消耗统计 ===");
                System.out.println("输入 Tokens: " + usage.get("input_tokens"));
                System.out.println("输出 Tokens: " + usage.get("output_tokens"));
                System.out.println("总计 Tokens: " + usage.get("total_tokens"));

            } else {
                System.err.println("请求失败，状态码: " + response.getStatus());
                System.err.println("响应内容: " + response.body());
            }

        } catch (Exception e) {
            System.err.println("请求发生异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}