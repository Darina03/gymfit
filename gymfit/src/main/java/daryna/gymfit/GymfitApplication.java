package daryna.gymfit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GymfitApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymfitApplication.class, args);
	}

}
