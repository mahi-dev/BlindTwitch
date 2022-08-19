package model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@DynamicUpdate
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
