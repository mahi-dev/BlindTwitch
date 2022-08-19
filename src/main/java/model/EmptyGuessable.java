package model;


import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

public class EmptyGuessable extends Guessable {
    @Override
    public String getName() {
        return "empty";
    }

    @Override
    public Blob getGuess() throws IOException, SQLException {
        return new SerialBlob(InputStream.nullInputStream().readAllBytes());
    }
}
