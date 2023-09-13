package aa.frieze.wateringflowersbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableCaching
@EnableScheduling
@EnableJpaAuditing
@EnableTransactionManagement
@EntityScan(basePackages = "aa.frieze.wateringflowersbot.domain")
@SpringBootApplication(scanBasePackages = {"aa.frieze.wateringflowersbot"})
public class WateringFlowersBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(aa.frieze.wateringflowersbot.WateringFlowersBotApplication.class, args);
    }
}