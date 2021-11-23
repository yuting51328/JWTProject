package com.example.captcha.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class LoginController {


    @Autowired
    private DefaultKaptcha producer;


    @GetMapping("/captcha/image")
    public void getImage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();


        response.setContentType("image/jpeg");
        String text = producer.createText();
        BufferedImage bf = producer.createImage(text);
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bf, "jpg", jpegOutputStream);

        session.setAttribute("captchaStr", text);
        session.setAttribute("date", text);

        response.setHeader("Cache-Control", "no-store");

        response.setHeader("Pragma", "no-cache");

        response.setDateHeader("Expires", 0);

        response.setContentType("image/jpeg");

        try {

            ServletOutputStream servletOutputStream = response.getOutputStream();

            servletOutputStream.write(jpegOutputStream.toByteArray());

            servletOutputStream.flush();

            servletOutputStream.close();

        } catch (IOException e) {

            log.error("輸出驗證碼失敗", e);


        }
    }

    @PostMapping(value = "/captcha/checkImage", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> checkImage(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> req) throws IOException {
        HttpSession session = request.getSession();
        Map<String, String> tMap = new HashMap<>();
        tMap.put("result", "false");

        String inputCode = req.get("inputCode");
        log.info("inputCode --> {}", inputCode);
        if (inputCode != null && session.getAttribute("captchaStr") != null) {
            if (session.getAttribute("captchaStr").equals(inputCode)) {
                tMap.put("result", "true");

                CloseableHttpClient httpclient = HttpClients.createDefault();


                HttpPost httpPost = new HttpPost("http://127.0.0.1:10000/checkLoginNum");
                //Execute and get the response.
                HttpResponse httpResponse = httpclient.execute(httpPost);
                HttpEntity entity = httpResponse.getEntity();

                HttpPost httpPost2 = new HttpPost("http://127.0.0.1:10000/checkIp");
                //Execute and get the response.
                HttpResponse httpResponse2 = httpclient.execute(httpPost2);
                HttpEntity entity2 = httpResponse2.getEntity();

                tMap.put("checkLoginNum", EntityUtils.toString(entity, "UTF-8"));
                tMap.put("checkIp", EntityUtils.toString(entity2, "UTF-8"));
                return new ResponseEntity<>(tMap, HttpStatus.OK);

            }
        }


        return new ResponseEntity<>(tMap, HttpStatus.FORBIDDEN);
    }
}