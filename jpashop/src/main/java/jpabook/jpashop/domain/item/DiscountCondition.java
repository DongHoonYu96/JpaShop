package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Order;

public interface DiscountCondition {
    boolean isSatisfiedBy(Order order);
}
