package yousync.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@Configuration
public class CommonConfiguration {
    @Bean
    public Client client() {
        return ClientBuilder.newClient();
    }

    @Bean
    public ViewResolver viewResolver(){
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/");
        resolver.setSuffix(".html");
        return resolver;
    }
}
