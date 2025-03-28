package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController //data를 바로 json,xml으로 보냄
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            //기존 : LAZY => 프록시 객체는 진짜 데이터 안뿌림
            //강제초기화 => 프록시 객체가아닌 진짜객체(데이터) 가져와
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    //패치조인
    //문제 : 1대 다 조인 => 같은order가 2회씩 총4번 조회됨(중복조회)
    //해결 : distinct sql삽입 (같은엔티티 조회 -> 중복걸러줌)
    //패치조인의 단점 : 페이징 불가
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();

        for (Order order : orders) {
            System.out.println("order ref = " + order + " id = " + order.getId());
        }

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    //* V3. 1 엔티티를 조회해서 DTO로 변환 페이징고려
    // To One 관계 : 패치조인
    // 나머지(컬렉션 등)은 hibernate.default.betch_fetch_size, @BatchSize로 최적화
    //* (Where in 4, 11(id) 으로 한번에 땡겨옴!)
    //안하는경우 단점 : 1+N쿼리
    // 개선 : 1+1쿼리

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit
    ) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    @Data
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime localDateTime;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.localDateTime = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
            this.orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
            //단점 : 결국 외부에 엔티티 의존성이 높음, orderItem수정시 api 스펙이 다바뀜
            //       Dto한번 감싸는것만으로 안됨
            //해결 : 엔티티를 엔티티 Dto로 바꿔야함 / orderItem을 orderItemDto로 바꿔야함
        }
    }

    @Data   //no properties 오류해결 : @Getter(@Data) 만들어주기
    static class OrderItemDto {

        //필드에는 딱 필요한것만 정의!
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }
    //문제 : SQL실행횟수 너무많음
    //      order1번 * member,address(N번) * orderItem N번 * item N번(책의갯수만큼..)
    //해결 : 패치조인?

}



