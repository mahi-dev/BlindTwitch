package model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serial;
import java.math.BigDecimal;
import java.util.Optional;

@Entity
@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@DynamicUpdate
public class Setting {

    @Serial
    private static final long serialVersionUID = -2268893432978785549L;

    public static final Optional<Boolean> TRI_TRUE = Optional.of(true);
    public static final Optional<Boolean> TRI_FALSE = Optional.of(false);
    public static final Optional<Boolean> TRI_UNKNOWN = Optional.empty();

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long uid;
    @NonNull
    private final String name;
    private final Optional<Boolean> exactMatch;
    @NonNull
    private final BigDecimal winningPoint;
    @NonNull
    private final BigDecimal penalityPoint;
}