package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.util.Set;


@Entity
@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@DynamicUpdate
public class GameResponse implements Model<Long> {

    @Serial
    private static final long serialVersionUID = -2268893432978536669L;

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long uid;
    private final int position;
    @OneToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @NonNull
    private final Guessable proposition;
    @NonNull
    @NotBlank
    private final String response;
    @NonNull
    @OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    private final Set<Match> acceptedMatch;
    private final boolean exactMatch;
    private final boolean active;

}
