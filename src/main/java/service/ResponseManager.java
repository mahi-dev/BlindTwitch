package service;

import lombok.NonNull;
import model.GameResponse;
import model.Match;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResponseManager {

    public static boolean isMatch(@NonNull String userResponse,@NonNull GameResponse gameResponse){
        return (gameResponse.isExactMatch()) ?
             userResponse.trim().equalsIgnoreCase(gameResponse.getResponse().trim()) :
             Stream.of(gameResponse.getAcceptedMatch().stream().map(Match::getName).collect(Collectors.toSet())
                        , Set.of(gameResponse.getResponse()))
                .flatMap(Collection::stream).anyMatch(m -> userResponse.trim().equalsIgnoreCase(m.trim()));
    }
}
