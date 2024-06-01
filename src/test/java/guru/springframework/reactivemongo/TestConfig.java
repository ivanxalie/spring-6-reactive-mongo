package guru.springframework.reactivemongo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.reactive.server.WebTestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;

@TestConfiguration
public class TestConfig {


    @Bean
    WebTestClientBuilderCustomizer customizer() {
        return builder -> builder.apply(mockOAuth2Login());
    }
}
