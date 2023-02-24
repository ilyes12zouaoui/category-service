package ilyes.de.categoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients
@PropertySource("classpath:configuration/handler_not_found.properties")
@PropertySource("classpath:configuration/spring_actuator.properties")
@PropertySource("classpath:configuration/springdoc_endpoints.properties")
public class CategoryServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(CategoryServiceApplication.class);
	}
}
