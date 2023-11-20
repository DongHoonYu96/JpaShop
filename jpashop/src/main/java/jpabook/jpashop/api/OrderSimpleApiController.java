package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
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

    //문제 : 1 + N 문제
    //1(주문목록조회쿼리) + 2(주문목록2개에 대해 스트림 반복문2회)
    //주문목록[1] : 멤버1회 조회 + 배송1회 조회
    //주문목록[2] : 멤버1회 조회 + 배송1회 조회
    //결과 : 1 + 2 + 2 //O(5)
    //해결 : 패치조인(version3)
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream() //스트림으로 바꾸기
                .map(o -> new SimpleOrderDto(o))//order를 Dto로 바꾸기
                .collect(Collectors.toList()); //리스트로바꾸기

        return result;
    }

    //패치조인 : 한번에 필요한거 다떙겨오는 함수구현&&jpa sql작성 => 1+N 문제 해결
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); //LAZY 초기화(영속성컨텍스트에없음 -> db쿼리날림)
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //LAZY 초기화(영속성컨텍스트에없음 -> db쿼리날림)
        }
    }
}
