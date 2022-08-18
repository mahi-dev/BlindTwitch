package model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serial;
import java.util.List;


@Entity
@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class GameResponseModel implements Model<Long> {

    @Serial
    private static final long serialVersionUID = -2268893432970108975L;

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long uid;
    @NonNull
    private final int order;
    @NonNull
    private final String response;
    @NonNull
    private final List<String> acceptedMatch;
    @NonNull
    private final boolean exactMatch;
    @NonNull
    private final boolean active;
}
