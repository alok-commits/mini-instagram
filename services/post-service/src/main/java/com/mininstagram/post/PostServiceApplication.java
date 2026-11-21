package com.mininstagram.post;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages={"com.mininstagram.post","com.mininstagram.common"})
public class PostServiceApplication { public static void main(String[] args) { SpringApplication.run(PostServiceApplication.class,args); } }
