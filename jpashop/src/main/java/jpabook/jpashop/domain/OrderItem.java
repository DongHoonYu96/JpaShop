package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import jpabook.jpashop.domain.common.Money;
import jpabook.jpashop.domain.common.jpa.MoneyConverter;
import jpabook.jpashop.domain.item.DiscountPolicy;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Convert(converter = MoneyConverter.class)
    @Column(name="price")
    private Money price;

    @Column(name = "quantity")
    private int quantity;

    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setPrice(new Money(orderPrice));
        orderItem.setQuantity(quantity);

        item.removeStock(quantity);
        return orderItem;
    }

    //==비즈니스 로직==//
    public void cancel() {
        getItem().addStock(quantity);  //재고수량 원복
    }

    //==조회 로직==//
    public Money calculateAmounts() {
        Money money = item.calculateItemFee();
        return money.multiply(quantity);
    }
}
