package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.context.annotation.Lazy;

import java.io.Serial;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class GameModel implements Model<Long> {

    @Serial
    private static final long serialVersionUID = -2268893432970108975L;

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long uid;
    @NonNull
    @NotBlank
    private String name;
    @NonNull
    @Lazy
    @OneToMany(fetch = FetchType.LAZY)
    private Set<GameResponseModel> responses;
}
