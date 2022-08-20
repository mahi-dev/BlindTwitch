package controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import model.Game;
import org.springframework.stereotype.Controller;
import service.ServiceClient;

import java.io.IOException;
import java.net.URL;

@Controller
@RequiredArgsConstructor
public class ImportExportController {

    private final ServiceClient.ImportExportService service;

    void storeGameResponse(@NonNull String name, @NonNull URL filePath) throws ServiceClient.Exception{
        service.storeGameResponse(name, filePath);
    }

    void serveGameResponse(@NonNull String id, @NonNull URL filePath) throws ServiceClient.Exception, IOException{
        service.serveGameResponse(id, filePath);
    }

    void serveGameResponse(@NonNull Game game, @NonNull URL filePath) throws ServiceClient.Exception, IOException{
        service.serveGameResponse(game, filePath);
    }

    void serveAllGames(@NonNull URL filePath) throws ServiceClient.Exception, IOException{
        service.serveAllGames(filePath);
    }

}
