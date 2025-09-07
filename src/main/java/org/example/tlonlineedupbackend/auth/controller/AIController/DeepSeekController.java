package org.example.tlonlineedupbackend.auth.controller.AIController;

import org.example.tlonlineedupbackend.auth.util.OllamaUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DeepSeekController {

    @PostMapping("/deepSeek")
    public String deepSeek(@RequestParam String someParam) {
        String res = OllamaUtil.chatDeepSeek("deepseek-r1:7b", someParam); //开发
//        String res = OllamaUtil.chatDeepSeek("deepseek-r1:1.5b", someParam); //部署
        return res;
    }
}
