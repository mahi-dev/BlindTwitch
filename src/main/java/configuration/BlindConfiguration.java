package configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.List;

@ConstructorBinding
@ConfigurationProperties(prefix = "configuration.properties")
public record BlindConfiguration(String provider, String clientId, String clientSecret, String clientToken, List<String> channels) {
}
