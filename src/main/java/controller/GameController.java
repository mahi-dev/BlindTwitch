package controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import model.Game;
import model.GameResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import service.ServiceClient;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GameController {

    public final ServiceClient.GameService service;

    @ModelAttribute
    GameResponse setupForm () {
        return new GameResponse();
    }

    @GetMapping("/game/{id}")
    public String showGames(@PathVariable("id") String id, Model model) throws ServiceClient.Exception {
        model.addAttribute("game", service.getGame(id).get(0));
        return "game";
    }

    List<Game> getAllGames() throws ServiceClient.Exception{
        return service.getAllGames();
    }

    List<Game> getGame(@NonNull String id) throws ServiceClient.Exception{
        return service.getGame(id);
    }

    void storeGame(@NonNull Game response) throws ServiceClient.Exception{
         service.storeGame(response);
    }

    void deleteGame(@NonNull String id) throws ServiceClient.Exception{
        service.deleteGame(id);
    }
}
