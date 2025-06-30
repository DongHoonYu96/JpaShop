package jpabook.jpashop.domain.item.pricing;

import jpabook.jpashop.domain.common.Money;
import jpabook.jpashop.domain.item.DiscountPolicy;
import jpabook.jpashop.domain.item.Item;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("NONE")
public class NoneDiscountPolicy extends DiscountPolicy {
    @Override
    protected Money getDiscountAmount(Item item) {
        return Money.ZERO;
    }
}
