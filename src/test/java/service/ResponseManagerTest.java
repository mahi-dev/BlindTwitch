package service;

import model.EmptyGuessable;
import model.GameResponse;
import model.Match;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

class ResponseManagerTest {

    @Test
    void should_exact_match(){
        Assertions.assertTrue(ResponseManager.isMatch("response",new GameResponse(0,
                new EmptyGuessable(),
                "response",
                Collections.emptySet(),
                true,
                true)));
    }

    @Test
    void should_match(){
        Assertions.assertTrue(ResponseManager.isMatch("res",new GameResponse(0,
                new EmptyGuessable(),
                "response",
                Set.of(new Match("res"),new Match("reponse"),new Match("responses")),
                false,
                true)));
    }

    @Test
    void should_not_match(){
        Assertions.assertFalse(ResponseManager.isMatch("res",new GameResponse(0,
                new EmptyGuessable(),
                "response",
                Set.of(new Match("res"),new Match("reponse"),new Match("responses")),
                true,
                true)));
    }
}