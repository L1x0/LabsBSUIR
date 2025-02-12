package by.astakhau.bonuslab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication
public class BonusLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(BonusLabApplication.class, args);
    }
}
