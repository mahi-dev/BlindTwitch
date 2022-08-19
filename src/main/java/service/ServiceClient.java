package service;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import configuration.BlindConfiguration;
import lombok.NonNull;
import model.GameModel;
import model.GameResponseModel;

import java.io.IOException;
import java.io.Serial;
import java.net.URL;
import java.util.List;

public class ServiceClient {

    public interface UserService {

        boolean isUnknowUser(@NonNull String userId);
        void createUser(@NonNull String userId,@NonNull String username);
    }

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
        List<GameModel> getAllGames() throws Exception;
        List<GameModel> getGame(@NonNull String id) throws Exception;
        void storeGame(@NonNull GameModel response) throws Exception;
        void deleteGame(@NonNull String id) throws Exception;
    }

    public interface ResponseService {
        List<GameResponseModel> getGameResponse(@NonNull String id) throws Exception;
        void storeGameResponse(@NonNull GameResponseModel response) throws Exception;
        void addAcceptedResponse(@NonNull GameResponseModel gameResponse, @NonNull String ...responses) throws Exception;
        default void addAcceptedResponse(@NonNull GameResponseModel gameResponse, @NonNull List<String> responses)  throws Exception {
            this.addAcceptedResponse(gameResponse, responses.toArray(new String[0]));
        }
        void deleteGameResponse(@NonNull String id) throws Exception, NumberFormatException;
    }

    public interface ImportExportService {
        void storeGameResponse(@NonNull String name, @NonNull URL filePath) throws Exception;
        void serveGameResponse(@NonNull String id, @NonNull URL filePath) throws Exception, IOException;
        void serveGameResponse(@NonNull GameModel game, @NonNull URL filePath) throws Exception, IOException;
        void serveAllGames(@NonNull URL filePath) throws Exception, IOException;
    }
}
