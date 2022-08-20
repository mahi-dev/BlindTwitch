package controller;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import event.ApplicationEvents;
import event.EventListener;
import event.EventManager;
import lombok.SneakyThrows;
import message.MessageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import service.AuthenticateTwitchClient;
import service.ServiceClient;

@Controller
public class BlindController  implements EventListener {

    public final AuthenticateTwitchClient client;
    public final MessageManager messageManager;

    private boolean isGameStarted = false;
    private String currentGameId = "0";
    private int currentResponseOrder = 0;

    @Autowired
    public BlindController(AuthenticateTwitchClient client, MessageManager messageManager) {
        this.client = client;
        this.messageManager = messageManager;
        EventManager.getInstance().addEventListener(ApplicationEvents.MESSAGE_RECEIVE, this);
        EventManager.getInstance().addEventListener(ApplicationEvents.START_GAME, this);
    }

    @SneakyThrows
    @Override
    public void onNotify(String eventType, Object[] args) {
        var event = (ChannelMessageEvent) args[0];
        var gameId= (String) args[1];
        var responseOrder= (int) args[2];
        switch (eventType){
            case ApplicationEvents.START_GAME:
                startGame(gameId, responseOrder);
                break;
            case ApplicationEvents.STOP_GAME:
                stopGame();
                break;
            default:
                var isUserGuess = isUserGuess(event);
        }
    }

    private boolean isUserGuess(ChannelMessageEvent arg) throws ServiceClient.Exception {
        return isGameStarted &&
                messageManager.isUserGuess(currentGameId, currentResponseOrder,arg);
    }

    public void startGame(String gameId, int responseOrder){
        isGameStarted = true;
        currentGameId = gameId;
        currentResponseOrder = responseOrder;
    }

    public void stopGame(){
        isGameStarted = false;

    }
}
