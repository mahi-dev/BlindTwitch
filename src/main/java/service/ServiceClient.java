package service;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import configuration.BlindConfiguration;
import lombok.NonNull;
import model.*;

import java.io.IOException;
import java.io.Serial;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Set;

public class ServiceClient {

    public static class Exception extends java.lang.Exception {
        @Serial
        private static final long serialVersionUID = 8732690520984084906L;
        public Exception(String message, Throwable cause) {
            super(message, cause);
        }
        public Exception(String message) {
            super(message);
        }
        public Exception(Throwable cause) {
            super(cause);
        }
    }

    public interface UserService {
        Set<ChannelUser> getUsers();
        ChannelUser getUser(@NonNull String userId) throws Exception;
        boolean isUnknowUser(@NonNull String userId);
        void deleteUser(@NonNull String userId);
        void createUser(@NonNull String userId,@NonNull String username, boolean hasAdminRole);
        List<ChannelUser> getAdministratorUsers();
        Score getScore(@NonNull String userId) throws Exception;
    }

    public interface TwitchService {
        TwitchClient createTtwitchClient(@NonNull String clientId,
                                         @NonNull String clientSecret,
                                         @NonNull String provider,
                                         @NonNull String clientToken) throws Exception;

        TwitchClient createTtwitchClient(BlindConfiguration blindConfiguration);

        boolean isCredentialValid(@NonNull String provider, @NonNull String clientToken);
        boolean isCredentialValid(BlindConfiguration blindConfiguration);

        OAuth2Credential getAdditionalCredentialInformation(@NonNull String provider, @NonNull String clientToken) throws Exception;
    }

    public interface GameService {
        List<Game> getAllGames() throws Exception;
        List<Game> getGame(@NonNull String id) throws Exception;
        Setting getActiveSetting(@NonNull String id) throws Exception;
        void storeGame(@NonNull Game game) throws Exception;
        void deleteGame(@NonNull String id) throws Exception;
    }

    public interface ResponseService {
        List<GameResponse> getGameResponse(@NonNull String id) throws Exception;
        void storeGameResponse(@NonNull GameResponse response) throws Exception;
        void addAcceptedResponse(@NonNull GameResponse gameResponse, @NonNull String ...responses) throws Exception;
        default void addAcceptedResponse(@NonNull GameResponse gameResponse, @NonNull List<String> responses)  throws Exception {
            this.addAcceptedResponse(gameResponse, responses.toArray(new String[0]));
        }
        void deleteGameResponse(@NonNull String id) throws Exception, NumberFormatException;
    }

    public interface ImportExportService {
        void storeGameResponse(@NonNull String name, @NonNull URL filePath) throws Exception;
        void serveGameResponse(@NonNull String id, @NonNull URL filePath) throws Exception, IOException;
        void serveGameResponse(@NonNull Game game, @NonNull URL filePath) throws Exception, IOException;
        void serveAllGames(@NonNull URL filePath) throws Exception, IOException;
    }


    public interface MessageService {
        boolean isUserGuess(@NonNull String gameId, int responseOrder, @NonNull ChannelMessageEvent event)
                throws Exception;

        boolean isUserGuess(@NonNull String gameId, int responseOrder, @NonNull String userId,
                            @NonNull String userName, @NonNull String response) throws Exception;
    }


    public interface SettingsService {
        Set<Setting> getAllSettings();
        Set<Setting> getSettings(@NonNull String id);
        Set<Setting> getSettings(@NonNull Game game);
        void storeSetting(@NonNull Setting setting);
    }

    public interface ScoreService {
        void addToScore(@NonNull String userId, BigDecimal winningPoint) throws Exception;
        void removeFromScore(@NonNull String userId, BigDecimal removePoint) throws Exception;
        BigDecimal getActualScore(@NonNull String userId) throws Exception;
        BigDecimal getGeneralScore(@NonNull String userId) throws Exception;
        void resetAllActualScore() throws Exception;
        void resetAllGeneralScore() throws Exception;
        void resetActualScore(@NonNull String userId) throws Exception;
        void resetGeneralScore(@NonNull String userId) throws Exception;

    }
}
