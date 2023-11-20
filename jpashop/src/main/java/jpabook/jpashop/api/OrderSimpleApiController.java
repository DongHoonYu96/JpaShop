package jpabook.jpashop.api;

import java.util.List;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 다 : 1, 1:1, x:1 관계 에서의 성능최적화
 * <p>
 * Order Order -> Member  / Order - >(연관관계) Delivery
 */
@RestController //data를 바로 json,xml으로 보냄
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    //문제1 : 양방향 연관관계시 서로 타고 들어감 => 무한루프
    //해결1 : 엔티티클래스(멤버)에 들어가서 다:1 중 한쪽을 @JsonIgnore걸기
    //문제2 : 지연로딩 => 멤버가 프록시 객체임 , jackson은 이 프록시 객체를 json으로 변환못함=> 오류
    //해결2 : Hibernate5 를 빈으로 등록
    //문제3 : 프록시 맴버는 null 값이 json에 찍힘
    //해결 3: .getName => Lazy 강제초기화
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();    //이름까지 끌고와야함 => 강제로 DB조회함 (Lazy 강제초기화)
            order.getDelivery().getAddress();   //Lazy 강제초기화
        }
        return all;
    }
}
