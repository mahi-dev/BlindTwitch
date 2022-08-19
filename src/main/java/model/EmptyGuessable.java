package model;


import lombok.SneakyThrows;

import javax.sql.rowset.serial.SerialBlob;
import java.io.InputStream;
import java.sql.Blob;

public class EmptyGuessable extends Guessable {
    @Override
    public String getName() {
        return "empty";
    }

    @Override
    @SneakyThrows
    public Blob getGuess() {
        return new SerialBlob(InputStream.nullInputStream().readAllBytes());
    }
}
