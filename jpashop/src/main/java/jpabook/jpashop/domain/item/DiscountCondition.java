package jpabook.jpashop.domain.item;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="discount_condition")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DiscountCondition {

    @Id @GeneratedValue
    private Long id;

    protected abstract boolean isSatisfiedBy(Item item);
}
