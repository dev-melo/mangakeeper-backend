package dev.flmelo.mangakeeper.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class MangakeeperBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MangakeeperBackendApplication.class, args);

	}

}
