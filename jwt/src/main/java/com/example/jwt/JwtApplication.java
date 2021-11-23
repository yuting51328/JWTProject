package com.example.jwt;

import com.example.jwt.task.CheckVerifyTimer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;

import java.util.Timer;

@EnableEurekaClient
@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class JwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtApplication.class, args);

        long delay = 3000L; // 延遲開始執行的時間（毫秒），延遲3秒
        long period = 3000L; // 重複的時間（毫秒），間隔1秒

        Timer repeatTimer = new Timer();
        repeatTimer.schedule(new CheckVerifyTimer(), delay, period);
    }

    @Bean

    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		/*return builder.routes()
				.route(p ->
						p.path("/users/**")
								.uri("http://localhost:8081/api/users")
				).build();*/
        RouteLocatorBuilder.Builder routes = builder.routes();
        RouteLocatorBuilder.Builder servicePovider = routes.route("accept",
                r -> r
                        .path("/page")
                        .uri("http://127.0.0.1:8081/")
        );

        return servicePovider.build();

    }

}
