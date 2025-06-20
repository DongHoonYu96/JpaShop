package jpabook.jpashop;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.UploadFile;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    public static class InitService {

        private final EntityManager em;

        public void dbInit1() {
            Member member = createMember("bisu", "123456", "userA", "서울", "1", "1111");
            em.persist(member);

            Book book1 = createBook("자바 ORM 표준 JPA 프로그래밍", 10000, 100);
            book1.getImages().add(new UploadFile("jpa.png", "jpa.png"));
            em.persist(book1);

            Book book2 = createBook("클린코더", 20000, 100);
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

        public static Book createBook(String name, int price, int stockQuantity) {
            return Book.builder().
                    name(name).
                    price(price).
                    stockQuantity(stockQuantity).
                    build();
        }

        public void dbInit2() {
            Member member = createMember("bingsu", "123456", "userB", "진주", "2", "2222");
            em.persist(member);

            Book book1 = createBook("오브젝트", 20000, 200);
            book1.getImages().add(new UploadFile("object.png", "object.png"));
            em.persist(book1);

            Book book2 = createBook("친절한 sql 튜닝", 40000, 300);
            book2.getImages().add(new UploadFile("kind_sql_tunning.png", "kind_sql_tunning.png"));
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        public void dbInit3() {
            Member member = createMember("admin", "admin!", "admin", "진주", "2", "2222");
            em.persist(member);
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


