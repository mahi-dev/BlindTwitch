import com.github.twitch4j.TwitchClientBuilder;
import configuration.BlindConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;
import service.AuthenticateTwitchClient;

@SpringBootApplication
@ConfigurationPropertiesScan({"service"})
@ComponentScan({"configuration" })
public class BlindApplication {

    @Bean
    @Lazy
    public TwitchClientBuilder  ClientBuilder(){
        return TwitchClientBuilder.builder();
    }

    @Bean
    @Lazy
    public BlindConfiguration  BlindConfiguration(){
        return BlindConfiguration();
    }

    @Bean
    @Lazy
    public AuthenticateTwitchClient authenticateTwitchClient(BlindConfiguration blindConfiguration, TwitchClientBuilder clientBuilder){
        return new AuthenticateTwitchClient( blindConfiguration,  clientBuilder);
    }

    public static void main(final String[] args) {
        SpringApplication.run(BlindApplication.class, args);
    }
}
