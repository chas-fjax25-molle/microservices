package com.example.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {

            var headers = exchange.getRequest().getHeaders();
            String forwarded = headers.getFirst("X-Forwarded-For");

            if (forwarded != null && !forwarded.isBlank()) {
                // take FIRST real client IP
                return Mono.just(forwarded.split(",")[0].trim());
            }

            var remote = exchange.getRequest().getRemoteAddress();
            if (remote != null && remote.getAddress() != null) {
                return Mono.just(remote.getAddress().getHostAddress());
            }

            return Mono.just("unknown");
        };
    }
}
