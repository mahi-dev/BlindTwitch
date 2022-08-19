package service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import model.GameResponseModel;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.GameResponseRepository;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ResponseService implements ServiceClient.ResponseService {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseService.class);

    private final GameResponseRepository repository;

    @Override
    public List<GameResponseModel> getGameResponse(@NonNull String id) throws ServiceClient.Exception {
        return (repository.findById(Long.parseLong(id)).isPresent())?
                List.of(repository.findById(Long.parseLong(id)).get()):
                Collections.emptyList();
    }

    @Override
    @Transactional
    public void storeGameResponse(@NonNull GameResponseModel response) throws ServiceClient.Exception {
        repository.saveAndFlush(response);
    }

    @Override
    @Transactional
    public void addAcceptedResponse(@NonNull GameResponseModel gameResponse, @NotNull String... responses)
            throws ServiceClient.Exception {
        repository.saveAndFlush(
                new GameResponseModel(
                    gameResponse.getOrder(),
                    gameResponse.getProposition(),
                    gameResponse.getResponse(),
                    Stream.of(gameResponse.getAcceptedMatch().toArray(new String[0]), responses)
                            .flatMap(this::flatten)
                            .map(s->(String) s)
                            .collect(Collectors.toSet()),
                    gameResponse.isExactMatch(),
                    gameResponse.isActive()
                )
        );
    }

    private Stream<Object> flatten(Object[] array) {
        return Arrays.stream(array)
                .flatMap(o -> o instanceof Object[]? flatten((Object[])o): Stream.of(o));
    }

    @Override
    @Transactional
    public void deleteGameResponse(@NonNull String id) throws ServiceClient.Exception {
        repository.deleteById(Long.parseLong(id));
    }

}