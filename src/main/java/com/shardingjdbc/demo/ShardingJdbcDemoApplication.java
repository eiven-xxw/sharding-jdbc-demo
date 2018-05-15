package com.shardingjdbc.demo;

import com.shardingjdbc.demo.service.DemoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ShardingJdbcDemoApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(ShardingJdbcDemoApplication.class, args);
		//applicationContext.getBean(DemoService.class).demo();
	}
}
