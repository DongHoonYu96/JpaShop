package jpabook.jpashop.domain.item.pricing;

import jpabook.jpashop.domain.item.DiscountCondition;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("NONE")
@NoArgsConstructor
public class NoneCondition extends DiscountCondition {

    @Override
    protected boolean isSatisfiedBy(Item item) {
        return true;
    }
}
