package model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.util.Set;


@Entity
@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@DynamicUpdate
public class GameResponse implements Model<Long> {

    @Serial
    private static final long serialVersionUID = -2268893432970108975L;

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long uid;
    private final int position;
    @OneToOne(fetch = FetchType.LAZY)
    @NonNull
    private final Guessable proposition;
    @NonNull
    @NotBlank
    private final String response;
    @NonNull
    @OneToMany(fetch = FetchType.LAZY)
    private final Set<Match> acceptedMatch;
    private final boolean exactMatch;
    private final boolean active;

}
