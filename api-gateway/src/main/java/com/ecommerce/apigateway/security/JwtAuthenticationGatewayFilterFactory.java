package com.ecommerce.apigateway.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilterFactory.Config> {

    private final JwtService jwtService;

    public JwtAuthenticationGatewayFilterFactory(JwtService jwtService) {
        super(Config.class);
        this.jwtService = jwtService;
    }

    @Getter
    @Setter
    public static class Config {
        private List<String> roles;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String jwt = extractJwtFromCookies(exchange.getRequest());

            if (jwt == null || !jwtService.isTokenValid(jwt)) {
                return unauthorized(exchange);
            }

            Long userId = jwtService.extractUserId(jwt);
            List<String> authorities = jwtService.extractAuthorities(jwt);

            if (config.getRoles() != null && !config.getRoles().isEmpty()) {
                boolean hasAuthorities = authorities != null && authorities
                        .stream()
                        .anyMatch(config.getRoles()::contains);

                if (!hasAuthorities) {
                    return forbidden(exchange);
                }
            }

            ServerHttpRequest modifiedRequest = exchange
                    .getRequest()
                    .mutate()
                    .header("X-User-Id", userId.toString())
                    .header("X-User-Authorities", String.join(",", authorities))
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse().setComplete();
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);

        return exchange.getResponse().setComplete();
    }

    private String extractJwtFromCookies(ServerHttpRequest request) {
        HttpCookie jwtCookie = request.getCookies().getFirst("jwt");

        return jwtCookie == null ? null : jwtCookie.getValue();
    }
}
