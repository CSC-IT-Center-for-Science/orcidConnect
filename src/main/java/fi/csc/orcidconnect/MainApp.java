package fi.csc.orcidconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApp {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(MainApp.class);
		app.run(args);
	}
	
}
