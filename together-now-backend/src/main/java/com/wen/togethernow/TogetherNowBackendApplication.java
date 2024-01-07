package com.wen.togethernow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author wen
 */
@SpringBootApplication
@MapperScan("com.wen.togethernow.mapper")
@EnableScheduling
public class TogetherNowBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TogetherNowBackendApplication.class, args);
    }

}
