package service;


import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import configuration.BlindConfiguration;
import event.MessageEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthenticateTwitchClient implements ServiceClient.TwitchService{

    @Getter
    private final BlindConfiguration blindConfiguration;
    private final TwitchIdentityProvider twitchIdentityProvider;
    private final TwitchClientBuilder clientBuilder;

    @Getter
    private final TwitchClient twitchClient;
    private final ServiceClient.UserService userService;

    @Autowired
    public AuthenticateTwitchClient(BlindConfiguration blindConfiguration,
                                    TwitchIdentityProvider twitchIdentityProvider,
                                    TwitchClientBuilder clientBuilder,
                                    ServiceClient.UserService userService) {
        this.blindConfiguration = blindConfiguration;
        this.twitchIdentityProvider = twitchIdentityProvider;
        this.clientBuilder = clientBuilder;
        this.twitchClient = createTtwitchClient(blindConfiguration);
        this.userService = userService;
        this.registerEvents();
        this.joinChannel(blindConfiguration.channel());
    }

    @Override
    public TwitchClient createTtwitchClient(@NonNull String clientId,
                                            @NonNull String clientSecret,
                                            @NonNull String provider,
                                            @NonNull String clientToken) throws ServiceClient.Exception {
        return clientBuilder
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withEnableHelix(true)
                .withChatAccount(getAdditionalCredentialInformation(
                        provider,
                        clientToken))
                .withEnableChat(true)
                .withEnableGraphQL(true)
                .withEnableKraken(true)
                .build();
    }

    @Override
    @SneakyThrows
    public TwitchClient createTtwitchClient(BlindConfiguration blindConfiguration) {
        return  createTtwitchClient(blindConfiguration.clientId(),
                    blindConfiguration.clientSecret(),
                    blindConfiguration.provider(),
                    blindConfiguration.clientToken());
    }

    @Override
    public boolean isCredentialValid(@NonNull String provider,
                                      @NonNull String clientToken) {
        return twitchIdentityProvider.isCredentialValid(new OAuth2Credential(provider, clientToken)).orElse(false);
    }

    @Override
    public boolean isCredentialValid(BlindConfiguration blindConfiguration) {
        return isCredentialValid(blindConfiguration.provider(), blindConfiguration.clientToken());
    }

    @Override
    public OAuth2Credential getAdditionalCredentialInformation(@NonNull String provider, @NonNull String clientToken)
            throws ServiceClient.Exception {
        return twitchIdentityProvider.getAdditionalCredentialInformation(new OAuth2Credential(provider, clientToken))
                .orElseThrow(()-> new ServiceClient.Exception("Credentials Error"));
    }

    private void registerEvents() {
        new MessageEvent(twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class), userService);
    }

    private void joinChannel(String channel) {
        twitchClient.getChat().joinChannel(channel);
    }
}
