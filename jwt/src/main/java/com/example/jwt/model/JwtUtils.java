package com.example.jwt.model;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwt.status.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import javax.servlet.http.Cookie;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
public class JwtUtils {

    public ServerWebExchange createToken(ServerWebExchange exchange) {
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //get Ip Address
        InetAddress inetAddress = ((InetSocketAddress)request.getRemoteAddress()).getAddress();
        if (inetAddress instanceof Inet4Address)
            log.error("IPv4: " + inetAddress);

        else if (inetAddress instanceof Inet6Address)
            log.error("IPv6: " + inetAddress);
        else
            System.err.println("Not an IP address.");


        String ipAddress = inetAddress.getHostAddress() ;


        /*Random r = new Random();
        String ipAddress =  r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);

         */
        int time = 10 * 60 * 1000;
        Date timeOut  = new Date(System.currentTimeMillis() + time);

        Map tMap = UserCache.getInstance().getUserMap();
        tMap.put(ipAddress, timeOut);
        UserCache.getInstance().setUserMap(tMap);


       // request.getURI().toString()
        String access_token = JWT.create()
                .withSubject(ipAddress)
                .withExpiresAt(timeOut)
                .withIssuer(ipAddress)
                .sign(algorithm);

        String refresh_token = JWT.create()
                .withSubject(ipAddress)
                .withExpiresAt(timeOut)
                .withIssuer(ipAddress)
                .sign(algorithm);
        response.getHeaders().set("access_token", access_token);
        response.getHeaders().set("refresh_token", refresh_token);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);


        ResponseCookie cookie = ResponseCookie.from("token", refresh_token)
                .maxAge(time)
                .path("/")
                .httpOnly(true)
                .sameSite("strict")
                .build();
        response.addCookie(cookie);
       // response.getHeaders().set("Set-Cookie", "key=value; HttpOnly; SameSite=strict");
        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);
        log.error("New token is :{}", tokens);

        return exchange;
    }


}
