package jpabook.jpashop.domain.item.pricing;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.common.Money;
import jpabook.jpashop.domain.item.DiscountCondition;
import jpabook.jpashop.domain.item.DiscountPolicy;
import jpabook.jpashop.domain.item.Item;

public class PercentDiscountPolicy extends DiscountPolicy {
    private double percent;

    public PercentDiscountPolicy(double percent, DiscountCondition... conditions) {
        super(conditions);
        this.percent = percent;
    }

    @Override
    protected Money getDiscountAmount(Item item) {
        return Money.of(item.getPrice()).times(percent);
    }
}
