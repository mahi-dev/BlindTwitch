package configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "configuration")
public record BlindConfiguration(String provider,
                                 String clientId,
                                 String clientSecret,
                                 String clientToken,
                                 String channel,
                                 String redirectUrl) {}
