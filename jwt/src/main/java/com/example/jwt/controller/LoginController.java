package com.example.jwt.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.jwt.model.JwtUtils;
import com.example.jwt.status.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
public class LoginController {

    //測試用
    @RequestMapping(value = "/authentication", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(ServerHttpRequest request, ServerHttpResponse response) throws Exception {

        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());

        String access_token = JWT.create()
                .withSubject(request.getRemoteAddress().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .withIssuer(request.getURI().toString())
                .sign(algorithm);

        String refresh_token = JWT.create()
                .withSubject(request.getRemoteAddress().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .withIssuer(request.getURI().toString())
                .sign(algorithm);
        response.getHeaders().set("access_token", access_token);
        response.getHeaders().set("refresh_token", refresh_token);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);


        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);

        return ResponseEntity.ok(tokens);
    }

    @RequestMapping(value = "/authorizise", method = RequestMethod.POST)
    public ResponseEntity<?> verifyToken(ServerWebExchange exchange, ServerHttpRequest request, ServerHttpResponse response) throws Exception {

        //Verify
        log.info("custom global filter");

        MultiValueMap<String, HttpCookie> cookieMap = request.getCookies();

        String authorizationHeader = null;
        if (StringUtils.isAllBlank(request.getHeaders().getFirst("AUTHORIZATION"))) {
            try {
                authorizationHeader = "Bearer " + cookieMap.getFirst("token").getValue();
                log.error("Cookie token is {}:", authorizationHeader);
            } catch (Exception ex) {
                log.error("Cookie Error is {}:", ex.getMessage());
            }

        } else {
            authorizationHeader = request.getHeaders().getFirst("AUTHORIZATION");
        }

        //authorizationHeader = request.getHeaders().getFirst("AUTHORIZATION");
        log.error("authorizationHeader is :{}", authorizationHeader);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            try {
                String token = authorizationHeader.substring("Bearer ".length());
                if (StringUtils.isAllBlank(token)) {
                    throw new Exception("token no value");
                }
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);
                String remoteAddress = decodedJWT.getSubject();

                log.error("remoteAddress is :{}", remoteAddress);
                return ResponseEntity
                        .status(OK)
                        .body("auth success");
            } catch (Exception ex) {
                log.error("Error logging in:{}", ex.getMessage());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", ex.getMessage());

                response.getHeaders().set("error_message", ex.getMessage());
                response.setStatusCode(FORBIDDEN);
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                //DataBuffer db = new DefaultDataBufferFactory().wrap(ex.getMessage().getBytes(StandardCharsets.UTF_8));

                return ResponseEntity
                        .status(FORBIDDEN)
                        .body(error);

            }
        } else if (UserCache.getInstance().getUserMap().size() < UserCache.USER_LOGIN_NUM) {
            exchange = new JwtUtils().createToken(exchange);

            return ResponseEntity
                    .status(OK)
                    .body("new cookie");
        } else {
            return ResponseEntity
                    .status(FORBIDDEN)
                    .body("no token founded");
        }

    }

    @RequestMapping(value = "/checkLoginNum", method = RequestMethod.POST)
    public ResponseEntity<?> checkLoginNum() throws Exception {

        return ResponseEntity
                .status(OK)
                .body(UserCache.getInstance().getUserMap().size() == UserCache.USER_LOGIN_NUM);
    }

    @RequestMapping(value = "/checkIp", method = RequestMethod.POST)
    public ResponseEntity<?> checkIp( ServerHttpRequest request) throws Exception {


        return ResponseEntity
                .status(OK)
                .body(UserCache.getInstance().getUserMap().containsKey(((InetSocketAddress)request.getRemoteAddress()).getAddress().toString()));
    }

}

