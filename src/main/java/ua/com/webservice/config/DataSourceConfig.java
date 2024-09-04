package ua.com.webservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DataSourceConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceConfig.class);
    private static final String REGEX = ":";

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public DataSource postgresDataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl != null) {
            try {
                URI dbUri = new URI(databaseUrl);
                dbUrl = "jdbc:postgresql://" + dbUri.getHost() + REGEX + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";
                username = dbUri.getUserInfo().split(REGEX)[0];
                password = dbUri.getUserInfo().split(REGEX)[1];
            }
            catch (URISyntaxException | NullPointerException e) {
                LOGGER.warn("Invalid DATABASE_URL: {}", databaseUrl);
                return null;
            }
        }
        else {
            LOGGER.info("Working with local PostgreSQL database: {}", dbUrl);
        }

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        dataSourceBuilder.url(dbUrl);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        return dataSourceBuilder.build();
    }
}