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
    public final ServiceClient.ScoreService scoreManager;
    public final ServiceClient.GameService gameService;

    private boolean isGameStarted = false;
    private String currentGameId = "0";
    private int currentResponseSerie = 0;

    @Autowired
    public BlindController(AuthenticateTwitchClient client, MessageManager messageManager,
                           ServiceClient.ScoreService scoreManager, ServiceClient.GameService gameService) {
        this.client = client;
        this.messageManager = messageManager;
        this.scoreManager = scoreManager;
        this.gameService = gameService;
        EventManager.getInstance().addEventListener(ApplicationEvents.MESSAGE_RECEIVE, this);
        EventManager.getInstance().addEventListener(ApplicationEvents.START_GAME, this);
        EventManager.getInstance().addEventListener(ApplicationEvents.STOP_GAME, this);
    }

    @SneakyThrows
    @Override
    public void onNotify(String eventType, Object[] args) {
        var event = (ChannelMessageEvent) args[0];
        var gameId= (String) args[1];
        var responseSerie= (int) args[2];
        switch (eventType){
            case ApplicationEvents.START_GAME:
                startGame(gameId, responseSerie);
                break;
            case ApplicationEvents.STOP_GAME:
                stopGame();
                break;
            case ApplicationEvents.MESSAGE_RECEIVE:
            default:
                play(event);
                break;
        }
    }

    private boolean isUserGuess(ChannelMessageEvent event) throws ServiceClient.Exception {
        return isGameStarted &&
                messageManager.isUserGuess(currentGameId, currentResponseSerie, event);
    }

    public void startGame(String gameId, int responseSerie){
        isGameStarted = true;
        currentGameId = gameId;
        currentResponseSerie = responseSerie;
    }

    public void stopGame(){
        isGameStarted = false;
    }

    public void play(ChannelMessageEvent event) throws ServiceClient.Exception {
        if(isUserGuess(event) && isGameStarted) {
            scoreManager.addToScore(event.getUser().getId(),
                    gameService.getActiveSetting(currentGameId).getWinningPoint());
            stopGame();
        }
    }
}
