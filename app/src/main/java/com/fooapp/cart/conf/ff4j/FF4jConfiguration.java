package com.fooapp.cart.conf.ff4j;

import lombok.extern.slf4j.Slf4j;
import org.ff4j.FF4j;
import org.ff4j.redis.RedisConnection;
import org.ff4j.store.EventRepositoryRedis;
import org.ff4j.store.FeatureStoreRedis;
import org.ff4j.store.PropertyStoreRedis;
import org.ff4j.web.FF4jDispatcherServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static net.logstash.logback.argument.StructuredArguments.value;

@Configuration
@Slf4j
public class FF4jConfiguration extends SpringBootServletInitializer {

    private static final String DEFAULT_CONSOLE = "/ff4j-web-console/*";

    @Value("${redis.server:localhost}")
    private String redisServer;

    @Value("${redis.port:6379}")
    private Integer redisPort;

    @Bean
    public FF4j configureFF4j() {
        FF4j ff4j = new FF4j();

        log.info("[starting] FF4jConfiguration.configureFF4j() for redis: {}:{}"
            ,value("redisHost",redisServer)
            ,value("redisPort", redisPort));

        RedisConnection redisConnection = new RedisConnection(redisServer, redisPort);

        ff4j.setFeatureStore(new FeatureStoreRedis(redisConnection));
        ff4j.setPropertiesStore(new PropertyStoreRedis(redisConnection));
        ff4j.setEventRepository(new EventRepositoryRedis(redisConnection));

        for (Features feature : Features.values()) {
            if (!ff4j.exist(feature.getKey())) {
                log.info("CommissionFeature {} created with value {}: ",
                        value("feature", feature.getKey()),
                        value("value", feature.isDefaultValue()));
                ff4j.createFeature(feature.getKey(), feature.isDefaultValue(), feature.getDescription());
            }
        }

        return ff4j;
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean(@Autowired final FF4j ff4j) {
        return new ServletRegistrationBean(getFF4JServlet(ff4j), DEFAULT_CONSOLE);
    }

    private FF4jDispatcherServlet getFF4JServlet(FF4j ff4j) {
        FF4jDispatcherServlet ff4jDispatcherServlet = new FF4jDispatcherServlet();
        ff4jDispatcherServlet.setFf4j(ff4j);
        return ff4jDispatcherServlet;

    }
}
