package com.example.captcha.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Properties;
//Kaptcha配置
@Configuration
public class KaptchaConfig {
    @Bean
    public DefaultKaptcha producer() {
        //Properties類
        Properties properties = new Properties();
        // 圖片邊框
        properties.setProperty("kaptcha.border", "yes");
        // 邊框顏色
        properties.setProperty("kaptcha.border.color", "105,179,90");
        // 字體顏色
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        // 圖片寬
        properties.setProperty("kaptcha.image.width", "110");
        // 圖片高
        properties.setProperty("kaptcha.image.height", "40");
        // 字體大小
        properties.setProperty("kaptcha.textproducer.font.size", "30");
        // session key
        properties.setProperty("kaptcha.session.key", "code");
        // 驗證碼長度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 字體
      //  properties.setProperty("kaptcha.textproducer.font.names", "宋體,楷體,微軟雅黑");
        //圖片幹擾
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.DefaultNoise");
        //Kaptcha 使用上述配置
        Config config = new Config(properties);
        //DefaultKaptcha對象使用上述配置, 並返回這個Bean
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
