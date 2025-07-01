package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.common.Money;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javax.persistence.DiscriminatorType.*;
import static javax.persistence.InheritanceType.*;
import static lombok.AccessLevel.*;

@Entity
@Table(name = "discount_policy")
@Inheritance(strategy = JOINED)
@DiscriminatorColumn(name = "policy_type", discriminatorType = STRING)
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode
@Getter
public abstract class DiscountPolicy {

    @Id @GeneratedValue
    private Long id;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "policy_id", nullable = false)
    private List<DiscountCondition> conditions = new ArrayList<>();

    public DiscountPolicy(DiscountCondition... conditions) {
        this.conditions = Arrays.asList(conditions);
    }

    public Money calculateDiscountAmount(Item item) {
        for(DiscountCondition each : conditions) {
            if (each.isSatisfiedBy(item)) {
                return getDiscountAmount(item);
            }
        }
        return Money.ZERO;
    }

    abstract protected Money getDiscountAmount(Item item);
}
