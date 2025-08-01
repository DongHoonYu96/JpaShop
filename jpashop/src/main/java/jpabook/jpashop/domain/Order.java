package jpabook.jpashop.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import jpabook.jpashop.domain.common.Money;
import jpabook.jpashop.domain.common.jpa.MoneyConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //기본생성자 protected로 막아버림. 생성메서드로만 생성하도록
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @Convert(converter = MoneyConverter.class)
    @Column(name = "total_amounts")
    private Money totalAmounts;

    private LocalDateTime orderDate;

    private OrderStatus status; //주문상태 [ORDER, CANCEL]

    //연관관계 편의 메서드
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==// 복잡한생성은 생성메서드로 해결!
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) { //...문법 == 유사리스트
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비즈니스 로직==//
    public void cancel() {
        verifytNotYetShipped();
        if (delivery.isDeliveryComplete()) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    private void verifytNotYetShipped() {
        if (delivery.isDeliveryShipping()) {
            throw new IllegalStateException("배송중인 상품은 취소가 불가능합니다.");
        }
        if (delivery.isDeliveryComplete()) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
    }

    //==조회 로직==//
    public Money getTotalPrice() {
        return orderItems.stream()
                .map(OrderItem::calculateAmounts)
                .reduce(Money.ZERO, Money::plus);
    }
}
