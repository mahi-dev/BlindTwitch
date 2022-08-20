package service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import model.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.GameRepository;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class GameService implements ServiceClient.GameService {

    private static final Logger LOG = LoggerFactory.getLogger(GameService.class);

    private final GameRepository repository;

    @Override
    public List<Game> getAllGames() throws ServiceClient.Exception {
        return repository.findAll();
    }

    @Override
    public List<Game> getGame(@NonNull String id) throws ServiceClient.Exception {
        return (repository.findById(Long.parseLong(id)).isPresent())?
                List.of(repository.findById(Long.parseLong(id)).get()):
                Collections.emptyList();
    }

    @Override
    public void storeGame(@NonNull Game response) throws ServiceClient.Exception {
        repository.saveAndFlush(response);
    }

    @Override
    public void deleteGame(@NonNull String id) throws ServiceClient.Exception {
        repository.deleteById(Long.parseLong(id));
    }
}