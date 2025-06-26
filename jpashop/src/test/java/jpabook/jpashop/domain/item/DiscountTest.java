package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.common.Money;
import jpabook.jpashop.domain.item.pricing.AmountDiscountPolicy;
import jpabook.jpashop.domain.item.pricing.PercentDiscountPolicy;
import jpabook.jpashop.domain.item.pricing.PeriodCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class DiscountTest {

    @Test
    @DisplayName("할인 정책을 지정하지 않은경우 NoneDiscountPolicy가 적용되어 상품의 가격이 그대로 반환된다.")
    void createItem() {
        //given
        Book book = new Book("오브젝트", 10000, 10, "조영호", "123456");
        Member member = new Member("testUser", new Address("Test City", "Test Street", "12345"), "id", "password");
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        OrderItem orderItem=OrderItem.createOrderItem(book,book.getPrice(),3);
        Order order=Order.createOrder(member,delivery,orderItem);

        //when
        Money discountMoney = book.calculateItemFee(order);

        //then
        assertEquals(Money.of(10000), discountMoney);
        assertEquals(Money.of(30000), order.getTotalPrice()); // 10000 * 3
    }

    @Test
    @DisplayName("매주 금요일에는 AmountDiscountPolicy가 적용되어 상품의 가격에서 각각 1000원이 할인된다.")
    void createItem2() {
        //given
        Book book = new Book("Test Item", 10000, 10, "조영호", "오브젝트",
                new AmountDiscountPolicy(Money.of(1000),
                    new PeriodCondition(
                        DayOfWeek.FRIDAY,
                        LocalTime.of(0, 0),
                        LocalTime.of(23, 59)
                    )
        ));

        Member member = new Member("testUser", new Address("Test City", "Test Street", "12345"), "id", "password");
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        OrderItem orderItem=OrderItem.createOrderItem(book,book.getPrice(),3);
        Order order=Order.createOrder(member,delivery,orderItem);
        order.setOrderDate(LocalDateTime.of(2023, 10, 6, 12, 0)); // 금요일

        //when
        Money discountMoney = book.calculateItemFee(order);

        //then
        assertEquals(Money.of(9000), discountMoney);
        assertEquals(Money.of(27000), order.getTotalPrice()); // (10000 - 1000) * 3
    }

    @Test
    @DisplayName("매주 목요일에는 PercentDiscountPolicy가 적용되어 상품의 가격에서 각각 30%씩 할인된다.")
    void createItem3() {
        //given
        Book book = new Book("Test Item", 10000, 10, "조영호", "오브젝트",
                new PercentDiscountPolicy(0.3,
                        new PeriodCondition(
                                DayOfWeek.THURSDAY,
                                LocalTime.of(0, 0),
                                LocalTime.of(23, 59)
                        )
                ));

        Member member = new Member("testUser", new Address("Test City", "Test Street", "12345"), "id", "password");
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        OrderItem orderItem=OrderItem.createOrderItem(book,book.getPrice(),3);
        Order order=Order.createOrder(member,delivery,orderItem);
        order.setOrderDate(LocalDateTime.of(2023, 10, 5, 12, 0)); // 목요일

        //when
        Money discountMoney = book.calculateItemFee(order);

        //then
        assertEquals(Money.of(7000), discountMoney);
        assertEquals(Money.of(21000), order.getTotalPrice()); // (10000 - 1000) * 3
    }
}