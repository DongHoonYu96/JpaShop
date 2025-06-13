package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() {
        //given
        Member member = createMember();

        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount=2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder=orderRepository.findOne(orderId);

        //(메세지, 기대값, 실제값)
        assertEquals("상품 주문시 상태가 ORDER여야 한다.", OrderStatus.ORDER,getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야 한다.",1,getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격*수량이어야 한다.",10000*orderCount,getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다.",8,book.getStockQuantity());
    }

    @Test
    public void 주문취소(){
        //given
        Member member=createMember();
        Book item = createBook("시골 JPA", 10000, 10);

        int orderCount=2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //when
        orderService.cancleOrder(orderId);

        //then
        Order getOrder=orderRepository.findOne(orderId);

        assertEquals("주문 취소시 상태는 CANCLE이어야",OrderStatus.CANCEL,getOrder.getStatus());
        assertEquals("주문이 취소된 상품은 재고가 증가해야",10,item.getStockQuantity());
    }

    //이 예외가 터져야한다.
    @Test(expected = NotEnoughStockException.class)
    @DisplayName("재고를 초과하여 주문하는 경우 예외가 발생해야 한다.")
    public void 상품주문_재고수량초과(){
        //given
        Member member=createMember();
        Item item = createBook("시골 JPA",10000,10);

        int orderCount=11;

        //when
        orderService.order(member.getId(),item.getId(),orderCount);

        //then
        fail("재고부족예외가 터져야한다.");
    }

    @Test(expected = IllegalStateException.class)
    @DisplayName("배송중인 주문을 취소하는 경우 예외가 발생해야 한다.")
    public void 주문취소_배송중() {
        // Given
        Member member = createMember();
        Book book = createBook("시골 JPA", 10000, 10);
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        Order order = orderRepository.findOne(orderId);

        // 배송 상태를 SHIPPING으로 설정
        order.getDelivery().startShipping();
        order.cancel();

        fail("배송중인 주문은 취소할 수 없으므로 예외가 발생해야 한다.");
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = Book.builder()
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member=new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울","경기","123-123"));
        member.setLoginId("bisu");
        member.setPassword("123456");
        em.persist(member);
        return member;
    }
}