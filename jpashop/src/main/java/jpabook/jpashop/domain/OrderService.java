package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    @Transactional(readOnly = false)
    public Long order(Long memberId, Long itemId, int count){
        boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();
        log.info("Actual class: {}", this.getClass());
        log.info("[{}] Transaction Active: {}", Thread.currentThread().getName(), isActive);
        //엔티티 조회
        Member member=memberRepository.findOne(memberId);
        Item item=itemRepository.findOne(itemId);

        // 주문 전의 엔티티 상태 로깅
        log.info("Before Order - Item ID: {}, Stock: {}", item.getId(), item.getStockQuantity());

        //배송정보 생성
        Delivery delivery=new Delivery();
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem=OrderItem.createOrderItem(item,item.getPrice(),count);
//        itemRepository.save(item);

        //주문 생성
        /*
        문제 : 다른개발자가 기본생성자 사용하면 안됨, 생성메서드만 사용하도록 하고싶음
        해결 : Order 기본생성자에 Protected 걸어
        해결2 : 롬복 : @NoArgsConstructor(access= AccessLevel.PROTECTED)
         */
        Order order=Order.createOrder(member,delivery,orderItem);
        //new Order() XXX

        //주문 저장
        /* CASCADE
        문제 : delivery, orderitem은 persist안함?
        해결 : order클래스에 cascade걸려잇는멤버 => order persist -> 자동persist
        문제1 : orderitem, delivery을 order에서만 참조하는 경우에만 사용할것!
        문제2 : delivery, orderitem을 여러군데서 사용하는경우?
        해결 : 별도의 repository => 각각 persist
         */

//        orderRepository.saveAndFlush(order);
        orderRepository.save(order);
        // 주문 저장 후 엔티티 상태 로깅 (flush 전)
        log.info("After Order Save - Item ID: {}, Stock: {}", item.getId(), item.getStockQuantity());
        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancleOrder(Long orderId){
        //주문 엔티티 조회
        Order order=orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
        /*
        문제 : JPA안쓰면, 실제 stock 을 바꾸는 update SQL 짜야함
        해결 : JPA사용시, 변수바꾸는것만으로 알아서 해줌(dirty check)
         */
    }

    /**
     * 검색
     */
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAllByString(orderSearch);
    }
}
