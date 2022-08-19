package model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.*;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

@Entity
@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class GuessableMovie extends Guessable implements Model<Long>, Guessable.Element {
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long uid;
    @NonNull
    private final String name;
    @Lob
    @NonNull
    private final Blob guess;

    @Override
    public <T> T accept(ElementVisitor<T> visitor) throws SQLException, IOException {
        return visitor.visit(Type.MOVIE, name, guess.getBinaryStream());
    }
}
