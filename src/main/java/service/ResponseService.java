package service;

import csv.CsvReader;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import model.GameResponseModel;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import repository.GameResponseRepository;

import javax.transaction.Transactional;
import java.io.FileReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ResponseService implements ServiceClient.Service {

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
                    gameResponse.getResponse(),
                    Stream.of(gameResponse.getAcceptedMatch().toArray(new String[0]), responses)
                            .flatMap(this::flatten)
                            .map(s->(String) s)
                            .collect(Collectors.toList()),
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

    @SneakyThrows
    @Override
    @Transactional
    public void storeGameResponse(@NonNull URL filePath) throws ServiceClient.Exception {
        new CsvReader(new FileReader(filePath.toString()))
                .rows()
                .filter(Objects::nonNull)
                .filter(row -> row.size() >= 6)
                .forEach(row ->
                    repository.saveAndFlush(
                        new GameResponseModel(
                            Integer.parseInt(row.get(1)),
                            row.get(2),
                            List.of(row.get(3).split("|")),
                            Boolean.parseBoolean(row.get(4)),
                            Boolean.parseBoolean(row.get(5))
                        )
                    )
        );
    }
}