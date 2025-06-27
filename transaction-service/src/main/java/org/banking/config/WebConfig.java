package org.banking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Banking Transaction Service Api")
                        .version("1.0.0")
                        .description("Banking Transaction Service RESTful API doc")
                );
    }


    public void addViewControllers(ViewControllerRegistry viewControllerRegistry)
    {
        viewControllerRegistry.addRedirectViewController("/","swagger-ui.html");
        viewControllerRegistry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }
}
