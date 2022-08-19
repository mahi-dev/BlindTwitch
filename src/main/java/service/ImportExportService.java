package service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import model.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.csv.CsvReader;
import utils.csv.CsvWriter;
import utils.storage.DiskMultiPartFile;
import utils.storage.FileSystemStorage;

import javax.sql.rowset.serial.SerialBlob;
import javax.transaction.Transactional;
import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ImportExportService implements ServiceClient.ImportExportService {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseService.class);
    private static final String CSV = ".csv";
    private static final String EMPTY_PATH = "empty/path";
    private static final String DELIMITER = "|";
    private final ServiceClient.GameService gameService;
    private final FileSystemStorage fileSystemStorage;

    @SneakyThrows
    @Override
    @Transactional
    public void storeGameResponse(@NonNull String name, @NonNull URL filePath) throws ServiceClient.Exception {
        try {
            gameService.storeGame(new GameModel(name,
                    new CsvReader(new FileReader(filePath.toString()))
                            .rows()
                            .filter(Objects::nonNull)
                            .filter(row -> row.size() >= 7)
                            .map(row -> new GameResponseModel(
                                            Integer.parseInt(row.get(1)),
                                            createGuessable(name, row.get(2)),
                                            row.get(3),
                                            Arrays.stream(row.get(4).split(DELIMITER)).map(Match::new).collect(Collectors.toSet()),
                                            Boolean.parseBoolean(row.get(5)),
                                            Boolean.parseBoolean(row.get(6))
                                    )
                            ).collect(Collectors.toSet())
            ));
        }catch (Exception e){
            LOG.error("Error during Csv storage {0}",e);
            throw new ServiceClient.Exception(e);
        }
    }

    @Override
    public void serveGameResponse(@NonNull String id, @NonNull URL filePath) throws ServiceClient.Exception, IOException {
        serveGameResponse(gameService.getGame(id).stream().findFirst().orElseThrow(()-> new ServiceClient.Exception(id)), filePath);
    }

    @Override
    public void serveGameResponse(@NonNull GameModel game, @NonNull URL filePath) throws ServiceClient.Exception, IOException {
        try {
            var exportableResponse = game.getResponses().stream()
                    .map(r-> List.of(
                            String.valueOf(r.getPosition()),
                            getPath(r.getProposition()),
                            r.getResponse(),
                            r.getAcceptedMatch().stream().map(Match::getName).collect(Collectors.joining(DELIMITER)),
                            Boolean.valueOf(r.isExactMatch()).toString(),
                            Boolean.valueOf(r.isActive()).toString()
                    )).collect(Collectors.toList());
            var csvWriter = new CsvWriter(new FileWriter(filePath +"_" +game.getName()+ CSV));
            for(var response : exportableResponse){
                csvWriter.writeRow(response);
            }
        }catch (Exception e){
            LOG.error("Error during Csv download {0}",e);
            throw new ServiceClient.Exception(e);
        }
    }

    @Override
    public void serveAllGames( @NonNull URL filePath) throws ServiceClient.Exception, IOException {
        for (var game : gameService.getAllGames()) {
            serveGameResponse(game, filePath);
        }
    }

    private Guessable createGuessable( @NotNull String name,@NotNull String path)  {
        try {
            return new Guessable(name, new SerialBlob(getInputStream(path).readAllBytes()));
        } catch (IOException | SQLException e) {
            LOG.error("Error during reading file {0}",e);
            return new EmptyGuessable();
        }
    }

    private InputStream getInputStream(@NotNull String path)  {
        try {
            return new BufferedInputStream(new FileInputStream(path));
        }catch(FileNotFoundException e){
            LOG.error("Error during reading file {0}",e);
            return InputStream.nullInputStream();
        }
    }

    private String getPath(@NotNull Guessable proposition) {
        try{
            return fileSystemStorage.storeBlob(new DiskMultiPartFile(proposition.getName(), proposition.getGuess().getBinaryStream()));
        }catch (SQLException | IOException e){
            LOG.error("Error during file storage {0}",e);
            return EMPTY_PATH;
        }
    }
}

