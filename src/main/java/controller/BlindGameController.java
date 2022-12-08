package controller;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import event.ApplicationEvents;
import event.EventListener;
import event.EventManager;
import lombok.SneakyThrows;
import model.Game;
import model.GameResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.ServiceClient;

@Controller
public class BlindGameController implements EventListener {

    public final ServiceClient.TwitchService client;
    public final ServiceClient.MessageService messageManager;
    public final ServiceClient.ScoreService scoreManager;
    public final ServiceClient.GameService gameService;

    private boolean isGameStarted = false;
    private String currentGameId = "0";
    private int currentResponseSerie = 0;

    @ModelAttribute
    Game setupForm () {
        return new Game();
    }

    @Autowired
    public BlindGameController(ServiceClient.TwitchService client, ServiceClient.MessageService messageManager,
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

    @RequestMapping(value="/game/{id}/action", method= RequestMethod.POST, params="action=Play")
    public void playAction(@PathVariable("id") String id, Model model) throws ServiceClient.Exception {
        startGame(id, ((GameResponse)model).getPosition());
    }

    @RequestMapping(value="/game/{id}/edit", method= RequestMethod.POST)
    public String editAction(@PathVariable("id") String id, Model model) throws ServiceClient.Exception {
        return "game/" + id;
    }

    @RequestMapping(value="/game/{id}/action", method= RequestMethod.POST, params="action=Stop")
    public void stopAction() throws ServiceClient.Exception {
        stopGame();
    }


    @GetMapping("/games")
    public String showGames(Model model) throws ServiceClient.Exception {
        model.addAttribute("games", gameService.getAllGames());
        return "games";
    }

    @GetMapping("/games/game/{id}")
    public String showGame(@PathVariable("id") String id, Model model) throws ServiceClient.Exception {
            model.addAttribute("game", gameService.getGame(id).stream().findFirst()
                    .orElseThrow(()-> new ServiceClient.Exception(String.format("Game not Found with id: %s", id))));
        return "game/view";
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
        currentGameId = "";
        currentResponseSerie = -1;
    }

    public void play(ChannelMessageEvent event) throws ServiceClient.Exception {
        if(isUserGuess(event) && isGameStarted) {
            scoreManager.addToScore(event.getUser().getId(),
                    gameService.getActiveSetting(currentGameId).getWinningPoint());
                    var currentGameReponse =  gameService.getGame(currentGameId).stream().findFirst()
                            .orElseThrow(()->new ServiceClient.Exception(
                                    String.format("Can't access to game %s", currentGameId)))
                            .getResponses().stream()
                            .filter(r-> currentResponseSerie == r.getPosition()).findAny()
                            .orElseThrow(()->new ServiceClient.Exception(
                                    String.format("Can't access to response %s", currentResponseSerie)))
                            .getResponse();
            client.getTwitchClient().getChat().sendMessage(client.getBlindConfiguration().channel(),
                    String.format("Felicitation %s, tu es le premier à avoir eu la bonne response : %s !",
                            event.getUser().getId(), currentGameReponse));
            stopGame();
        }
    }
}
