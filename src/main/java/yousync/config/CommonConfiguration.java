package yousync.config;

import io.github.anoobizzz.youget.YouGet;
import io.github.anoobizzz.youget.exception.InitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import yousync.service.DirectoryResolverService;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@Configuration
public class CommonConfiguration {
    @Value("${settings.music.directory:#{null}}")
    private String musicDirectory;

    @Bean
    Client client() {
        return ClientBuilder.newClient();
    }

    //Circular complexity fix...
    @Bean
    ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/");
        resolver.setSuffix(".html");
        return resolver;
    }

    @Bean
    DirectoryResolverService directoryResolverService() {
        return new DirectoryResolverService(musicDirectory);
    }

    @Bean
    YouGet youGet() throws InitializationException {
        return new YouGet(8, directoryResolverService().getDownloadDirectory());
    }
}
