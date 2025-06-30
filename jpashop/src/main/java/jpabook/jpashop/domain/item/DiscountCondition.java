package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Order;

import javax.persistence.*;

@Entity
@Table(name="discount_condition")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DiscountCondition {

    @Id @GeneratedValue
    private Long id;

    protected abstract boolean isSatisfiedBy(Order order);
}
