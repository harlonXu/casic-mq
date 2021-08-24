package com.casic.mq;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class RabbitMQApplication {

   public static void main(String[] args) {
      SpringApplication.run(RabbitMQApplication.class, args);
   }
}
