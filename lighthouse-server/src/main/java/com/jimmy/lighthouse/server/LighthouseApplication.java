package com.jimmy.lighthouse.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Jimmy mailto:835490943@qq.com
 * @version 1.0.0
 * @since 2022-02-06
 */
@SpringBootApplication(scanBasePackages = {"com.jimmy.lighthouse.server"})
@MapperScan(basePackages = {"com.jimmy.lighthouse.server.mapper"})
public class LighthouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(LighthouseApplication.class, args);
    }
}
