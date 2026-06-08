package com.association;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 协会管理系统 - 启动类
 *
 * @author Association Management System
 * @since 2026-02-09
 */
@Slf4j
@SpringBootApplication
@MapperScan("com.association.mapper")
public class AssociationApplication {

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext context = SpringApplication.run(AssociationApplication.class, args);
        Environment env = context.getEnvironment();
        
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        
        log.info("\n----------------------------------------------------------\n\t" +
                "🚀 Startup Success!\n\t" +
                "Application: {}\n\t" +
                "Profile(s):  {}\n\t" +
                "Local:       http://localhost:{}{}\n\t" +
                "External:    http://{}:{}{}\n\t" +
                "API Docs:    http://localhost:{}{}/doc.html\n" +
                "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getActiveProfiles(),
                port, contextPath,
                ip, port, contextPath,
                port, contextPath);
    }
}
