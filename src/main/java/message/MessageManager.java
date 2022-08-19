package message;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import model.GameModel;
import model.GameResponseModel;
import service.ServiceClient;

import java.util.Collections;
import java.util.Set;

@RequiredArgsConstructor
public class MessageManager {

    @NonNull
    private final ServiceClient.GameService gameService;

    @NonNull
    private final ServiceClient.ResponseService responseService;

    @NonNull
    private final ServiceClient.UserService userService;

    public boolean isUserGuess(@NonNull String gameId, int responseOrder, @NonNull ChannelMessageEvent event) throws ServiceClient.Exception {
       if(userService.isUnknowUser(event.getUser().getId())){
           userService.createUser(event.getUser().getId(), event.getUser().getName());
       }
       return ResponseManager.isMatch(event.getMessage(), getGameResponse(gameId, responseOrder));
    }

    private GameResponseModel getGameResponse(String gameId, int responseOrder) throws ServiceClient.Exception {
        return getGameResponses(gameId).stream().filter(r->responseOrder == r.getPosition()).findAny()
                .orElseThrow(()-> new ServiceClient.Exception("Response Not Found"));
    }

    private Set<GameResponseModel> getGameResponses(String gameId) throws ServiceClient.Exception {
        return !gameService.getGame(gameId).isEmpty() ?
                gameService.getGame(gameId).stream().findFirst().map(GameModel::getResponses).get() :
                Collections.emptySet();
    }
}
