package utils;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import model.EmptyGuessable;
import model.Guessable;
import model.GuessableMovie;
import model.GuessableSong;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

@RequiredArgsConstructor
public class GuessableRenderer implements Guessable.ElementVisitor<Guessable> {

    @Override
    public Guessable visit(@Nonnull Guessable.Type type, String name, InputStream io) throws IOException, SQLException {
        return switch(type) {
            case MOVIE -> new GuessableMovie(name, new SerialBlob(io.readAllBytes()));
            case SONG -> new GuessableSong(name, new SerialBlob(io.readAllBytes()));
            default -> new EmptyGuessable();
        };
    }
}
