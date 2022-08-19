package controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import model.GameModel;
import org.springframework.stereotype.Controller;
import service.ServiceClient;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GameController {

    public final ServiceClient.GameService service;

    List<GameModel> getAllGames() throws ServiceClient.Exception{
        return service.getAllGames();
    }

    List<GameModel> getGame(@NonNull String id) throws ServiceClient.Exception{
        return service.getGame(id);
    }

    void storeGame(@NonNull GameModel response) throws ServiceClient.Exception{
         service.storeGame(response);
    }

    void deleteGame(@NonNull String id) throws ServiceClient.Exception{
        service.deleteGame(id);
    }
}
