package aa.frieze.wateringflowersbot.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class PersistenceConfig {

    @Value("${spring.datasource.db.name}")
    private String dbName;
    @Value("${spring.datasource.hikari.schema}")
    private String dbSchema;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.pool.size}")
    private Integer poolSize;
    @Value("${spring.datasource.idle.size}")
    private Integer idleSize;
    @Value("${spring.datasource.idle.timeout}")
    private Integer idleTimeout;

    @Bean
    public DataSource wateringFlowersBotDataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        try {
            PGSimpleDataSource dataSource = new PGSimpleDataSource();
            dataSource.setDatabaseName(dbName);
            dataSource.setURL(url);
            dataSource.setUser(username);
            dataSource.setPassword(password);
            dataSource.setCurrentSchema(dbSchema);
            hikariDataSource.setDataSource(dataSource);
            hikariDataSource.setMaximumPoolSize(poolSize);
            hikariDataSource.setMinimumIdle(idleSize);
            hikariDataSource.setIdleTimeout(idleTimeout);
            return hikariDataSource;
        } catch (Exception e) {
            log.error("Произошла ошибка инициализации контекста БД", e);
            return null;
        }
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(wateringFlowersBotDataSource());
    }
}
