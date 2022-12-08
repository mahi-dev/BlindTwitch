package controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import model.Game;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import service.ServiceClient;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

@Controller
@RequiredArgsConstructor
public class ImportExportController {

    @Value("${fileSystemStorage.basePath}")
    private Path fileSystemStorageBasePath;

    private final ServiceClient.ImportExportService service;

    @PostMapping("/games/import")
    public RedirectView importGame(HttpServletRequest request, @RequestParam("file") @NonNull MultipartFile multipartFile, Model model) {

        if (multipartFile.isEmpty()) {
            model.addAttribute("message", "Please select a CSV file to upload.");
            model.addAttribute("status", false);
        } else {
            try {
                var dir =  request.getServletContext().getRealPath("/targetFile.tmp");
                var file = new File(dir);
                multipartFile.transferTo(file);
                storeGameResponse("test", file);
            } catch (Exception ex) {
                model.addAttribute("message", "An error occurred while processing the CSV file.");
                model.addAttribute("status", false);
            }
        }
        return new RedirectView("/games");
    }

    void storeGameResponse(@NonNull String name, @NonNull File file) throws ServiceClient.Exception{
        service.storeGameResponse(name, file);
    }

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
