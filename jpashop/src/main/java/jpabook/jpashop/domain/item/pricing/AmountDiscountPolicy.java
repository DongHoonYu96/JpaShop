package jpabook.jpashop.domain.item.pricing;

import jpabook.jpashop.domain.common.Money;
import jpabook.jpashop.domain.item.DiscountCondition;
import jpabook.jpashop.domain.item.DiscountPolicy;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("AMOUNT")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
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
