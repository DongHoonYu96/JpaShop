package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
//@Transactional
public class OrderServiceConcurrencyTestOld {
    @Autowired
    EntityManager em;
    
    @Autowired
    OrderService orderService;

    @Autowired
    ItemRepository itemRepository;
    
    @Autowired
    PlatformTransactionManager transactionManager;
    
    @Autowired
    TestDataService testDataService; // 별도 서비스 주입

    @Test
    @DisplayName("10명이 5개씩 주문하면 재고가 50이 줄어야 한다.")
    @Commit
    @Transactional
    public void stock_decrease_concurrency() throws InterruptedException {
        // given - 데이터 설정을 위한 서비스 호출 (트랜잭션 적용됨)
        Book book = createBook("시골 JPA", 10000, 100);
        Member member = createMember();
        
        int orderCount = 1;
        int threadCount = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for(int i=0; i<threadCount; i++){
            executorService.submit(() -> {
                try {
                    orderService.order(member.getId(), book.getId(), orderCount);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then - 결과 확인을 위한 메소드 호출 (트랜잭션 적용됨)
        em.clear();
        Book findBook = em.find(Book.class, book.getId());
        assertThat(findBook.getStockQuantity()).isZero();
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
        em.persist(member);
        return member;
    }
}


