// package com.example.gateway;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
// import org.springframework.test.context.bean.override.mockito.MockitoBean;
// import org.springframework.test.web.reactive.server.WebTestClient;

// import com.example.common.security.JwtUtil;
// import com.github.tomakehurst.wiremock.junit5.WireMockTest;
// import static com.github.tomakehurst.wiremock.client.WireMock.*;
// import org.springframework.context.ApplicationContext;

// @WireMockTest(httpPort = 9090)
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
//         "spring.cloud.gateway.server.webflux.routes[0].id=user-service",
//         "spring.cloud.gateway.server.webflux.routes[0].uri=http://localhost:9090",
//         "spring.cloud.gateway.server.webflux.routes[0].predicates[0]=Path=/api/user-service/**",
//         "spring.cloud.gateway.server.webflux.routes[0].filters[0]=StripPrefix=2",

//         "spring.cloud.gateway.server.webflux.routes[0].filters[1].name=RequestRateLimiter",
//         "spring.cloud.gateway.server.webflux.routes[0].filters[1].args.key-resolver=#{@userKeyResolver}",
//         "spring.cloud.gateway.server.webflux.routes[0].filters[1].args.[redis-rate-limiter.replenishRate]=1",
//         "spring.cloud.gateway.server.webflux.routes[0].filters[1].args.[redis-rate-limiter.burstCapacity]=1",
//         "spring.cloud.gateway.server.webflux.routes[0].filters[1].args.[redis-rate-limiter.requestedTokens]=1",

//         "spring.data.redis.host=localhost",
//         "spring.data.redis.port=6379",
// })
// public class RateLimitingTest {

//     @MockitoBean
//     private JwtUtil jwtUtil;

//     @Autowired
//     private ApplicationContext context;

//     private WebTestClient client;

//     @Autowired
//     private ReactiveStringRedisTemplate redis;

//     @BeforeEach
//     void setup() {
//         setupWebTestClient();
//         setupWireMock();
//         clearRedis();
//     }

//     void setupWebTestClient() {
//         client = WebTestClient.bindToApplicationContext(context).build();
//     }

//     void setupWireMock() {
//         stubFor(
//                 get(urlEqualTo("/test"))
//                         .willReturn(
//                                 okJson("""
//                                         {
//                                         "status":"ok"
//                                         }
//                                         """)));
//     }

//     void clearRedis() {
//         redis.getConnectionFactory()
//                 .getReactiveConnection()
//                 .serverCommands()
//                 .flushAll()
//                 .block();
//     }

//     @Test
//     void testRateLimitingAllowsRequestsWithinCapacity() {

//         client.get()
//                 .uri("/api/user-service/test")
//                 .exchange()
//                 .expectStatus()
//                 .isOk();
//     }

//     @Test
//     void testRateLimitingRejectsExcessRequests() {

//         // first request consumes burst token
//         client.get()
//                 .uri("/api/user-service/test")
//                 .exchange()
//                 .expectStatus()
//                 .isOk();

//         // second immediately should fail
//         client.get()
//                 .uri("/api/user-service/test")
//                 .exchange()
//                 .expectStatus()
//                 .isEqualTo(429);
//     }
// }
