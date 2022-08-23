package model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor(force = true)
@DynamicUpdate
public class Score implements Model<Long> {

    @Serial
    private static final long serialVersionUID = -2268893432970105522L;

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long uid;
    @NotNull
    private  BigDecimal generalScore;
    @NotNull
    private  BigDecimal actualScore;

    public Score(@NotNull BigDecimal generalScore, @NotNull BigDecimal actualScore) {
        this.generalScore = generalScore;
        this.actualScore = actualScore;
    }
}
