package model;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
