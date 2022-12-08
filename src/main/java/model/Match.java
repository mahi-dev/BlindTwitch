package model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper=false)
@DynamicUpdate
public class Match implements Model<Long> {
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private Long uid;
    @NonNull
    private final String name;
}
