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

    @RequiredArgsConstructor
    public enum Match {
        TRUE(Optional.of(true)), FALSE(Optional.of(false)), UNKNOWN(Optional.empty());
        @NonNull private Optional<Boolean> value;
    }
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long uid;
    @NonNull
    private final String name;
    private final Match exactMatch;
    @NonNull
    private final BigDecimal winningPoint;
    @NonNull
    private final BigDecimal penalityPoint;
}