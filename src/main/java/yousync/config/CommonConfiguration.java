package yousync.config;

import io.github.anoobizzz.youget.YouGet;
import io.github.anoobizzz.youget.exception.InitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import yousync.service.ConfigurationService;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@Configuration
@ComponentScan(basePackages = {"yousync.ui", "yousync.service", "yousync.sources"})
@PropertySource("classpath:application.properties")
public class CommonConfiguration {
    @Value("${settings.music.directory:#{null}}")
    private String musicDirectory;

    @Bean
    Client client() {
        return ClientBuilder.newClient();
    }

    @Bean
    ConfigurationService configurationService() {
        return new ConfigurationService(musicDirectory);
    }

    @Bean
    YouGet youGet() throws InitializationException {
        return new YouGet(8, configurationService().getDownloadDirectory());
    }
}
