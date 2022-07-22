package com.practice.arch.commonarch;

import com.sun.jmx.mbeanserver.StandardMBeanSupport;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.management.NotCompliantMBeanException;

@SpringBootApplication(exclude = {
        RedissonAutoConfiguration.class})
@EnableAsync
public class CommonLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonLibraryApplication.class, args);
    }
}
