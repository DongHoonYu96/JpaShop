package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")   //테이블이름 이걸로해줘 (기본값:클래스명)
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name="order_id")
    private Long id;

    @ManyToOne //Order클래스입장에서 맴버는 1개임. Order(Many) : member(One)
    @JoinColumn(name="member_id")   //조인(매핑) 이걸로해줘
    private Member member;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems= new ArrayList<>();

    @OneToOne
    @JoinColumn(name="delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;    //자바클래스임, 주문시간

    private OrderStatus status; //주문상태 [ORDER, CANCEL]


}
