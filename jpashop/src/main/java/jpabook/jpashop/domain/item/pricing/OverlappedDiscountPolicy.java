package jpabook.jpashop.domain.item.pricing;

import jpabook.jpashop.domain.common.Money;
import jpabook.jpashop.domain.item.DiscountPolicy;
import jpabook.jpashop.domain.item.Item;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@DiscriminatorValue("OVERLAPPED")
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode
public class OverlappedDiscountPolicy extends DiscountPolicy {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "overlapped_policy_id")
    private List<DiscountPolicy> discountPolicies = new ArrayList<>();

    public OverlappedDiscountPolicy(DiscountPolicy... policies) {
        super(new NoneCondition());
        this.discountPolicies = Arrays.asList( policies);
    }

    @Override
    protected Money getDiscountAmount(Item item) {
        Money result = Money.ZERO;
        for (DiscountPolicy each : discountPolicies) {
            result = result.plus(each.calculateDiscountAmount(item));
        }
        return result;
    }
}
