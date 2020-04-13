package com.bishe.java;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bishe.java.mapper")
public class JavaApplication {

    public static void main(String[] args) {

        SpringApplication.run(JavaApplication.class, args
        );

    }

}
