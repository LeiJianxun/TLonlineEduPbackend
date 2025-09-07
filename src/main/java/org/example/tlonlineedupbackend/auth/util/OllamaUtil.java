package org.example.tlonlineedupbackend.auth.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * @date 2025-01-15 10:58:46
 */
@Slf4j
public class OllamaUtil {
    private static String DOMAIN = "http://localhost:11434/api/generate";

    public static String chatDeepSeek(String model, String question) {

        String url = DOMAIN;
        JSONObject body = new JSONObject();
        body.put("model", model);
        body.put("prompt", question);
        // 关闭流式返回，全部生成完再返回数据
        body.put("stream", false);
        String result = CommonUtil.postJson(url, body.toJSONString());
        log.info("【ollama-请求】 结果 {}", result);
        try {
            JSONObject resJson = JSONObject.parseObject(result);
            String response = resJson.getString("response");
            log.info("【ollama-请求】 结果 {}", response);
            return response;
        } catch (Exception e) {
            log.error("【ollama-请求】异常", e);
        }
        return "ok";
    }
}
