package model;


import jakarta.annotation.Nonnull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;

public abstract class Guessable {

    public enum Type{
        MOVIE,SONG,
    }

    public interface Element extends Serializable {
        <T> T accept(ElementVisitor<T> visitor) throws SQLException, IOException;
    }

    public interface ElementVisitor<T> {
        T visit(@Nonnull Type type, @Nonnull String name, @Nonnull InputStream io) throws IOException, SQLException;
    }

    public abstract String getName();
    public abstract Blob getGuess() throws IOException, SQLException;
}