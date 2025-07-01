package jpabook.jpashop;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.common.Money;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.UploadFile;
import jpabook.jpashop.domain.item.pricing.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
        initService.dbInit3();
        initService.dbInit4();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    public static class InitService {

        private final EntityManager em;

        public void dbInit1() {
            Member member = createMember("bisu", "123456", "userA", "서울", "1", "1111");
            em.persist(member);

            Book book1 = createBook("자바 ORM 표준 JPA 프로그래밍", 10000, 100, "김영한");
            book1.getImages().add(new UploadFile("jpa.png", "jpa.png"));
            em.persist(book1);

            Book book2 = createBook("클린코더", 20000, 100, "로버트 마틴");
            book2.getImages().add(new UploadFile("clean_coder.png", "clean_coder.png"));
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        public static Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }

        public static Book createBook(String name, int price, int stockQuantity, String author) {
            return Book.builder().
                    name(name).
                    price(price).
                    stockQuantity(stockQuantity).
                    author(author).
                    discountPolicy(new NoneDiscountPolicy()).
                    build();
        }

        public void dbInit2() {
            Member member = createMember("bingsu", "123456", "userB", "진주", "2", "2222");
            em.persist(member);
//
//            Book book1 = createBook("오브젝트", 20000, 200, "조영호");
//            book1.getImages().add(new UploadFile("object.png", "object.png"));
//            em.persist(book1);

            Book book2 = createBook("친절한 sql 튜닝", 40000, 300, "조시형");
            book2.getImages().add(new UploadFile("kind_sql_tunning.png", "kind_sql_tunning.png"));
            em.persist(book2);

            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem2);
            em.persist(order);
        }

        public void dbInit3() {
            Member member = createMember("admin", "admin!", "admin", "진주", "2", "2222");
            em.persist(member);
        }

        public void dbInit4() {
            Book book = new Book("오브젝트", 30000, 10, "조영호", "123456",
                    new PercentDiscountPolicy(0.3,
                            new NoneCondition()
                    ));
            book.getImages().add(new UploadFile("object.png", "object.png"));
            em.persist(book);

            Book book2 = new Book("스프링 시큐리티 인 액션", 30000, 100, "로렌티우 스필카", "123456",
                    new AmountDiscountPolicy(Money.of(2000),
                            new PeriodCondition(
                                    DayOfWeek.WEDNESDAY,
                                    LocalTime.of(0, 0),
                                    LocalTime.of(23, 59)
                            )
                    ));
            book2.getImages().add(new UploadFile("security.png", "security.png"));
            em.persist(book2);
        }

        public static Member createMember(String loginId, String password, String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setLoginId(loginId);
            member.setPassword(password);
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }
    }
}


