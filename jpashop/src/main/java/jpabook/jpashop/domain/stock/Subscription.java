package jpabook.jpashop.domain.stock;

import jpabook.jpashop.domain.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subscription {

    @Id @GeneratedValue
    @Column(name = "subscription_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    //==생성 메서드==//
    public static Subscription createSubscription(Member member, Stock stock) {
        Subscription subscription = new Subscription();
        subscription.setMember(member);
        subscription.setStock(stock);
        return subscription;
    }

    //==연관관계 편의 메서드==//
    private void setMember(Member member) {
        this.member = member;
        member.getSubscriptions().add(this);
    }

    private void setStock(Stock stock) {
        this.stock = stock;
    }
}
