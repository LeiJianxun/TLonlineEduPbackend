package org.example.tlonlineedupbackend.auth.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.example.tlonlineedupbackend.auth.entity.AnswerAI;
import org.example.tlonlineedupbackend.auth.repository.AnswerAiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class AIService {

    @Autowired
    private QuestionAiService questionService;

    @Autowired
    private AnswerAiRepository answerService;

    @Value("${zhipu.api.key}")
    private String apiKey;

    @Async
    public void evaluateAnswer(AnswerAI answer) {
        // 调用大模型 API（示例使用智谱清言）
        String prompt = String.format("请对以下答案进行评分（0 - 100 分）和情感分析以及情感颜色（根据情感分析的结果关键词，关键词正向积极且基本正确或完成准确的为'green'、反向消极且不正确的为'red'、两者中间的为'orange'）,按评分：xx 情感分析：xxx 情感颜色：xxx模板给我结果就可以：\n问题：%s\n答案：%s",
                questionService.getQuestionContent(answer.getQuestionId()),
                answer.getContent());

        try {
            // 创建 HttpClient 实例
            HttpClient httpClient = HttpClients.createDefault();
            // 创建 HttpPost 请求，指向智谱清言的 API 地址
            HttpPost httpPost = new HttpPost("https://open.bigmodel.cn/api/paas/v4/chat/completions");

            // 设置请求头
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + apiKey);

            // 构建请求体
            JSONObject requestBody = new JSONObject();
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);
            requestBody.put("model", "glm-4");
            requestBody.put("messages", messages);

            // 设置请求体内容
            StringEntity entity = new StringEntity(requestBody.toJSONString(), StandardCharsets.UTF_8);
            httpPost.setEntity(entity);

            // 执行请求并获取响应
            HttpResponse response = httpClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            // 解析响应结果
            JSONObject responseJson = JSON.parseObject(responseBody);
            JSONArray choices = responseJson.getJSONArray("choices");
            String responseText = choices.getJSONObject(0).getJSONObject("message").getString("content");

            AnswerEvaluation evaluation = parseEvaluation(responseText);

            answer.setScore(evaluation.getScore());
            answer.setEmotionAnalysis(evaluation.getEmotion());
            answer.setEmotionAnalysisColor(evaluation.getColor());
            answerService.save(answer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private AnswerEvaluation parseEvaluation(String response) {
        // 实现解析逻辑，返回包含分数和情感分析的对象
        // 示例代码，需要根据实际响应格式调整
        int score = 0;
        String emotion = "";
        String color = "";

        // 简单示例：假设响应格式为 "分数: 80, 情感: 积极"
        String[] parts = response.split("\n");
        if (parts.length == 3) {
            try {
                score = Integer.parseInt(parts[0].split("：")[1].trim());
                emotion = parts[1].split("：")[1].trim();
                color = parts[2].split("：")[1].trim();
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        return new AnswerEvaluation(score, emotion, color);
    }

    // 定义 AnswerEvaluation 类
    private static class AnswerEvaluation {
        private int score;
        private String emotion;
        private String color;

        public AnswerEvaluation(int score, String emotion, String color) {
            this.score = score;
            this.emotion = emotion;
            this.color = color;
        }

        public double getScore() {
            return score;
        }

        public String getEmotion() {
            return emotion;
        }

        public String getColor() {
            return color;
        }
    }
}