package configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "configuration")
public record BlindConfiguration(String provider,
                                 String clientId,
                                 String clientSecret,
                                 String clientToken,
                                 String channel,
                                 String redirectUrl) {}
