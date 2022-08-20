package service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import model.GameResponse;
import model.Match;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.GameResponseRepository;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ResponseService implements ServiceClient.ResponseService {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseService.class);

    private final GameResponseRepository repository;

    @Override
    public List<GameResponse> getGameResponse(@NonNull String id) throws ServiceClient.Exception {
        return (repository.findById(Long.parseLong(id)).isPresent())?
                List.of(repository.findById(Long.parseLong(id)).get()):
                Collections.emptyList();
    }

    @Override
    @Transactional
    public void storeGameResponse(@NonNull GameResponse response) throws ServiceClient.Exception {
        repository.saveAndFlush(response);
    }

    @Override
    @Transactional
    public void addAcceptedResponse(@NonNull GameResponse gameResponse, @NotNull String... responses)
            throws ServiceClient.Exception {
        repository.saveAndFlush(
                new GameResponse(
                    gameResponse.getPosition(),
                    gameResponse.getProposition(),
                    gameResponse.getResponse(),
                    Stream.of(gameResponse.getAcceptedMatch(), Set.of(responses).stream().map(Match::new)
                                    .collect(Collectors.toSet()))
                            .flatMap(Collection::stream)
                            .collect(Collectors.toSet()),
                    gameResponse.isExactMatch(),
                    gameResponse.isActive()
                )
        );
    }

    @Override
    @Transactional
    public void deleteGameResponse(@NonNull String id) throws ServiceClient.Exception {
        repository.deleteById(Long.parseLong(id));
    }
}