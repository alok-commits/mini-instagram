package com.mininstagram.like;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication(scanBasePackages={"com.mininstagram.like","com.mininstagram.common"})
public class LikeServiceApplication { public static void main(String[] args) { SpringApplication.run(LikeServiceApplication.class,args); } }
