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
        if(gameResponse.isExactMatch())
            return userResponse.trim().equalsIgnoreCase(gameResponse.getResponse().trim());
        return Stream.of(gameResponse.getAcceptedMatch().stream().map(Match::getName).collect(Collectors.toSet())
                        , Set.of(gameResponse.getResponse()))
                .flatMap(Collection::stream)
                .filter(m-> userResponse.trim().equalsIgnoreCase(m.trim()))
                .count() > 0;

    }
}
