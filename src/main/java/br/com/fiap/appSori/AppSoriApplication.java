package br.com.fiap.appSori;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication
public class AppSoriApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppSoriApplication.class, args);
	}

}
