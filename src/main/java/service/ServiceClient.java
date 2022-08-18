package service;

import lombok.NonNull;
import model.GameResponseModel;

import java.io.Serial;
import java.net.URL;
import java.util.List;

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

    public interface Service {

        List<GameResponseModel> getGameResponse(@NonNull String id) throws Exception;
        void storeGameResponse(@NonNull GameResponseModel response) throws Exception;
        void addAcceptedResponse(@NonNull GameResponseModel gameResponse, @NonNull String ...responses) throws Exception;
        default void addAcceptedResponse(@NonNull GameResponseModel gameResponse, @NonNull List<String> responses)  throws Exception {
            this.addAcceptedResponse(gameResponse, responses.toArray(new String[0]));
        }
        void deleteGameResponse(@NonNull String id) throws Exception, NumberFormatException;
        void storeGameResponse(@NonNull URL filePath) throws Exception;
    }
}
