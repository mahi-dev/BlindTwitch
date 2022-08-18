package service;


import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import configuration.BlindConfiguration;
import event.MessageEvent;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateTwitchClient {

    private final BlindConfiguration blindConfiguration;
    private final TwitchClientBuilder clientBuilder;

    @Getter
    private final TwitchClient twitchClient;

    @Autowired
    public AuthenticateTwitchClient(BlindConfiguration blindConfiguration, TwitchClientBuilder clientBuilder) {
        this.blindConfiguration = blindConfiguration;
        this.clientBuilder = clientBuilder;
        this.twitchClient = createTtwitchClient(blindConfiguration);
        this.registerEvents();
        this.joinChannels();
    }

    public TwitchClient createTtwitchClient(@NonNull String clientId,
                                            @NonNull String clientSecret,
                                            @NonNull String provider,
                                            @NonNull String clientToken){
        return clientBuilder
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withEnableHelix(true)
                .withChatAccount(new OAuth2Credential(
                        provider,
                        clientToken))
                .withEnableChat(true)
                .withEnableGraphQL(true)
                .withEnableKraken(true)
                .build();
    }

    public TwitchClient createTtwitchClient(BlindConfiguration blindConfiguration) {
        return  createTtwitchClient(blindConfiguration.clientId(),
                    blindConfiguration.clientSecret(),
                    blindConfiguration.provider(),
                    blindConfiguration.clientToken());
    }

    private void registerEvents() {
        new MessageEvent(twitchClient.getEventManager().getEventHandler(SimpleEventHandler.class));
    }

    private void joinChannels() {
        for (String channel : blindConfiguration.channels()) {
            twitchClient.getChat().joinChannel(channel);
        }
    }
}
