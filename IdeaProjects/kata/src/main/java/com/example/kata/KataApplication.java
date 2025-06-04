package com.example.kata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class KataApplication {

	private static final Logger logger = LoggerFactory.getLogger(KataApplication.class);

	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(KataApplication.class, args);
	}

	@PostConstruct
	public void logSwaggerUrl() {
		String portStr = env.getProperty("server.port");
		int port = 8080; // значение по умолчанию

		if (portStr != null) {
			try {
				port = Integer.parseInt(portStr);
			} catch (NumberFormatException e) {
				// если не удалось распарсить, оставляем порт по умолчанию
			}
		}

		String url = "http://localhost:" + port + "/swagger-ui.html";
		logger.info("Swagger UI доступен по адресу: {}", url);
	}
}