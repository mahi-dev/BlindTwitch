package model;


import lombok.SneakyThrows;

import java.io.InputStream;

public class EmptyGuessable extends Guessable {
    @Override
    public String getName() {
        return "empty";
    }

    @Override
    @SneakyThrows
    public byte[] getGuess() {
        return InputStream.nullInputStream().readAllBytes();
    }
}
