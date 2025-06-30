package jpabook.jpashop.repository;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;

import static jpabook.jpashop.InitDb.InitService.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    EntityManager em;

    @Test
    void findAllByQueryDsl() {
        //given
        Member member = createMember("bisu", "123456", "userA", "서울", "1", "1111");
        em.persist(member);

        Book book1 = createBook("JPA1 BOOK", 10000, 100, "김영한");
        em.persist(book1);

        Book book2 = createBook("JPA2 BOOK", 20000, 100, "김영한");
        em.persist(book2);

        OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
        OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);
        Delivery delivery = createDelivery(member);
        Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
        em.persist(order);

        OrderSearch orderSearch1 = new OrderSearch();
        orderSearch1.setMemberName("userA");
        orderSearch1.setOrderStatus(OrderStatus.ORDER);

        OrderSearch orderSearch2 = new OrderSearch();
        orderSearch2.setMemberName("userA");
        orderSearch2.setOrderStatus(OrderStatus.CANCEL);

        //when
        List<Order> orders1 = orderRepository.findAllByQueryDsl(orderSearch1);
        List<Order> orders2 = orderRepository.findAllByQueryDsl(orderSearch2);

        //then
        assertThat(orders1).hasSize(1)
            .extracting("member","delivery","status")
            .containsExactlyInAnyOrder(
                    tuple(member, delivery, OrderStatus.ORDER)
            );
        assertThat(orders2).isEmpty();
    }
}