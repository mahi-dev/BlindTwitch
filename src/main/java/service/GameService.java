package service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import model.DefaultSetting;
import model.Game;
import model.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.GameRepository;

import javax.transaction.Transactional;
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
    public Setting getActiveSetting(@NonNull String id) throws ServiceClient.Exception {
        return getGame(id).get(0).getSettings().stream().filter(s -> Setting.Match.TRUE.equals(s.getExactMatch()))
                .findFirst().orElse(new DefaultSetting());
    }

    @Transactional
    @Override
    public void storeGame(@NonNull Game game) throws ServiceClient.Exception {
        repository.saveAndFlush(game);
    }

    @Transactional
    @Override
    public void deleteGame(@NonNull String id) throws ServiceClient.Exception {
        repository.deleteById(Long.parseLong(id));
    }
}