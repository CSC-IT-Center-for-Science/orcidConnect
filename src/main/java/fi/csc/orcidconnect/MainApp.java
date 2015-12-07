package fi.csc.orcidconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.github.vbauer.herald.ext.spring.LogBeanPostProcessor;

@SpringBootApplication
public class MainApp {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(MainApp.class);
		app.run(args);
	}
	
	@Bean
    public LogBeanPostProcessor logBeanPostProcessor() {
        return new LogBeanPostProcessor();
    }
	
}
