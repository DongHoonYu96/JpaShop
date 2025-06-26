package jpabook.jpashop.domain.item.pricing;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.common.Money;
import jpabook.jpashop.domain.item.DiscountCondition;
import jpabook.jpashop.domain.item.DiscountPolicy;
import jpabook.jpashop.domain.item.Item;

public class AmountDiscountPolicy extends DiscountPolicy {

    private Money discountAmount;

    public AmountDiscountPolicy(Money discountAmount, DiscountCondition... conditions) {
        super(conditions);
        this.discountAmount = discountAmount;
    }

    @Override
    protected Money getDiscountAmount(Item item) {
        return discountAmount;
    }
}
