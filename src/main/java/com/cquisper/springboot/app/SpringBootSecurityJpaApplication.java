package com.cquisper.springboot.app;

import com.cquisper.springboot.app.service.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootSecurityJpaApplication implements CommandLineRunner {
    @Autowired
    private UploadFileService fileService;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootSecurityJpaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        fileService.deleteAll();
        fileService.init();
    }
}
