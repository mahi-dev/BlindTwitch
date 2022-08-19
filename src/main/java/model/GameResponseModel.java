package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serial;
import java.util.Set;


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
    @OneToOne(fetch = FetchType.LAZY)
    @NonNull
    private final Guessable proposition;
    @NonNull
    @NotBlank
    private final String response;
    @NonNull
    private final Set<String> acceptedMatch;
    @NonNull
    private final boolean exactMatch;
    @NonNull
    private final boolean active;

}
