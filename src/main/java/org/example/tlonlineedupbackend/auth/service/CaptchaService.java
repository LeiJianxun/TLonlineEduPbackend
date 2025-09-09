package org.example.tlonlineedupbackend.auth.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.tlonlineedupbackend.auth.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class CaptchaService {

    @Autowired
    private RedisUtil redisUtil;

    //验证码字符集
    public static final String CAPTCHA_CHARS = "23456789ABCDEFGHIJKLMNPQRSTUVWXYZ";
    public static final int CAPTCHA_LENGTH = 4; //验证码字符长度
    public static final int IMAGE_WIDTH = 120; //宽
    public static final int IMAGE_HEIGHT = 40; //高
    public static final int EXPIRE_TIME = 300; //验证码5分钟超时

    /**
     * 生成验证码图片
     */
    public void generateCaptcha(HttpServletResponse response) throws IOException {
        //验证码文本生成
        String captchaText = generateCaptchaText();

        //验证码唯一ID
        String captchaId = UUID.randomUUID().toString();

        //生成的验证码使用Redis存储，5分钟过期
        redisUtil.set("captcha:" + captchaId, captchaText, EXPIRE_TIME);

        //图片生成
        BufferedImage image = createCaptchaImage(captchaText);

        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        response.setHeader("Captcha-Id", captchaId); // 返回验证码ID

        // 6. 输出图片
        ImageIO.write(image, "PNG", response.getOutputStream());

    }

    /**
     *
     * @param captchaId 验证码Id
     * @param inputCode 输入验证码
     * @return
     */
    public ResponseEntity<Map<String, Object>> verifyCaptcha(String captchaId, String inputCode) {
        Map<String, Object> result = new HashMap();

        try {
            //从Redis获取正确验证码
            String correctCode = (String) redisUtil.get("captcha:" + captchaId);

            if (correctCode == null) {
                result.put("success", false);
                result.put("message", "验证码已过期，请重新获取");
                return ResponseEntity.internalServerError().body(result);
            }

            //验证码验证 - 忽略大小写
            if (correctCode.equalsIgnoreCase(inputCode)) {
                //验证成功后立即删除Redis中的验证码信息
                redisUtil.delete("captcha:" + captchaId);

                result.put("success", true);
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "验证码错误");
                return ResponseEntity.internalServerError().body(result);
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "验证码验证失败");
            return ResponseEntity.internalServerError().body(result);
        }

    }

    private String generateCaptchaText() {
        Random random = new Random();
        StringBuffer captchaText = new StringBuffer();

        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            captchaText.append(CAPTCHA_CHARS.charAt(random.nextInt(CAPTCHA_CHARS.length())));
        }

        return captchaText.toString();
    }

    private BufferedImage createCaptchaImage(String captchaText) {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        //设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //设置验证码图片填充背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

        //绘制干扰线
        Random random = new Random();
        g2d.setStroke(new BasicStroke(2));
        for (int i = 0; i < 5; i++) {
            g2d.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            int x1 = random.nextInt(IMAGE_WIDTH);
            int y1 = random.nextInt(IMAGE_HEIGHT);
            int x2 = random.nextInt(IMAGE_WIDTH);
            int y2 = random.nextInt(IMAGE_HEIGHT);
            g2d.drawLine(x1, y1, x2, y2);
        }

        Font[] fonts = {new Font("Arial", Font.BOLD, 28), new Font("Times New Roman", Font.BOLD, 28)};
        for (int i =0; i < captchaText.length(); i++) {
            g2d.setFont(fonts[random.nextInt(fonts.length)]);
            g2d.setColor(new Color(random.nextInt(100), random.nextInt(100), random.nextInt(100)));

            //随机旋转角度
            double angle = (random.nextDouble() - 0.5) * 0.5;
            int x = 20 + i * 25;
            int y = 25 + random.nextInt(10);

            g2d.rotate(angle, x, y);
            g2d.drawString(String.valueOf(captchaText.charAt(i)), x, y);
            g2d.rotate(-angle, x, y);
        }

        //绘制干扰点
        for (int i = 0; i < 50; i++) {
            g2d.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g2d.fillOval(random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT), 2, 2);
        }

        g2d.dispose();
        return image;

    }

}
