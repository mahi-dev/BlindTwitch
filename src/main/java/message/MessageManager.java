package message;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import model.Game;
import model.GameResponse;
import service.ResponseManager;
import service.ServiceClient;

import java.util.Collections;
import java.util.Set;

@RequiredArgsConstructor
public class MessageManager implements ServiceClient.MessageService{

    @NonNull
    private final ServiceClient.GameService gameService;

    @NonNull
    private final ServiceClient.ResponseService responseService;

    @NonNull
    private final ServiceClient.UserService userService;

    @Override
    public boolean isUserGuess(@NonNull String gameId, int responseSerie, @NonNull ChannelMessageEvent event)
            throws ServiceClient.Exception {
       return isUserGuess(gameId, responseSerie, event.getUser().getId(), event.getUser().getName(),event.getMessage());
    }

    @Override
    public boolean isUserGuess(@NonNull String gameId, int responseSerie, @NonNull String userId,
                               @NonNull String userName, @NonNull String response) throws ServiceClient.Exception {
        if(userService.isUnknowUser(userId)){
            userService.createUser(userId, userName, false);
        }
        return ResponseManager.isMatch(response, getGameResponse(gameId, responseSerie));
    }

    private GameResponse getGameResponse(String gameId, int responseSerie) throws ServiceClient.Exception {
        return getGameResponses(gameId).stream().filter(r->responseSerie == r.getPosition()).findAny()
                .orElseThrow(()-> new ServiceClient.Exception("Response Not Found"));
    }

    private Set<GameResponse> getGameResponses(String gameId) throws ServiceClient.Exception {
        return !gameService.getGame(gameId).isEmpty() ?
                gameService.getGame(gameId).stream().findFirst().map(Game::getResponses).get() :
                Collections.emptySet();
    }
}
