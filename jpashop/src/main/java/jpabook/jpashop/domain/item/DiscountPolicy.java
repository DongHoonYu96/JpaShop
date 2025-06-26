package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.common.Money;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DiscountPolicy {
    private List<DiscountCondition> conditions = new ArrayList<>();

    public DiscountPolicy(DiscountCondition... conditions) {
        this.conditions = Arrays.asList(conditions);
    }

    public Money calculateDiscountAmount(Order order, Item item) {
        for(DiscountCondition each : conditions) {
            if (each.isSatisfiedBy(order)) {
                return getDiscountAmount(item);
            }
        }
        return Money.ZERO;
    }

    abstract protected Money getDiscountAmount(Item item);
}
