package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.ItemRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
//@Transactional
public class ItemConcurrencyTest {

    @Autowired
    EntityManager em;

    @Autowired
    ItemRepository itemRepository;

    @PersistenceUnit
    EntityManagerFactory emf;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Test
    @Commit
    public void test() throws InterruptedException {
        // given
        int stockQuantity = 100;
        Book book = createBook(stockQuantity);
        int orderCount = 5;
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    book.removeStock(orderCount);
                    em.flush();
                }
                finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Item item = itemRepository.findOne(book.getId());
        assertThat(item.getStockQuantity()).isEqualTo(stockQuantity - orderCount * threadCount);
    }

    @Test
    public void test2() throws InterruptedException {
        // given - 메인 트랜잭션에서 처리
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        int stockQuantity = 100;
        Book book = createBook(stockQuantity);
        int orderCount = 5;
        int threadCount = 10;

        // 메인 트랜잭션 커밋
        transactionManager.commit(status);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    // 각 스레드마다 새로운 트랜잭션과 EntityManager 생성
                    TransactionStatus threadTx = transactionManager.getTransaction(new DefaultTransactionDefinition());
                    EntityManager threadEm = emf.createEntityManager();

                    try {
                        // 해당 스레드 트랜잭션에서 엔티티 다시 조회
                        Book threadBook = threadEm.find(Book.class, book.getId());
                        threadBook.removeStock(orderCount);

                        log.info("스레드 {} 재고 감소 처리: {}", Thread.currentThread().getName(), threadBook.getStockQuantity());

                        // 변경사항 저장 및 커밋
                        threadEm.flush();
                        transactionManager.commit(threadTx);
                    } catch (Exception e) {
                        transactionManager.rollback(threadTx);
                        log.error("오류 발생: {}", e.getMessage());
                    } finally {
                        threadEm.close();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // then - 결과 확인을 위한 새 트랜잭션
        status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        em.clear(); // 영속성 컨텍스트 초기화
        Item item = itemRepository.findOne(book.getId());
        log.info("최종 재고: {}", item.getStockQuantity());
        assertThat(item.getStockQuantity()).isEqualTo(stockQuantity - orderCount * threadCount);
        transactionManager.commit(status);
    }

    private Book createBook(int stockQuantity) {
        Book book = Book.builder()
                .name("테스트 책")
                .price(10000)
                .stockQuantity(stockQuantity)
                .author("김영한")
                .isbn("123456")
                .build();
        em.persist(book);
        return book;
    }

    // 별도의 테스트 데이터 DTO
    @Data
     static class TestDataDto {
        private Long bookId;
        private Long memberId;
    }

    // 테스트 데이터 관리용 서비스
    @Service
    @Slf4j
    static class TestDataService {
        @Autowired
        EntityManager em;

        @Autowired
        ItemRepository itemRepository;

        @Transactional
        public TestDataDto createTestData(int stockQuantity) {
            Book book = Book.builder()
                    .name("테스트 책")
                    .price(10000)
                    .stockQuantity(stockQuantity)
                    .author("김영한")
                    .isbn("123456")
                    .build();
            em.persist(book);

            Member member = new Member();
            member.setName("회원1");
            member.setAddress(new Address("서울","경기","123-123"));
            em.persist(member);

            TestDataDto dto = new TestDataDto();
            dto.setBookId(book.getId());
            dto.setMemberId(member.getId());
            return dto;
        }

        @Transactional(readOnly = true)
        public void verifyFinalStock(Long itemId, int expectedStock) {
            em.clear(); // 영속성 컨텍스트 초기화
            Item item = itemRepository.findOne(itemId);
            log.info("최종 재고: {}", item.getStockQuantity());
            assertEquals(expectedStock, item.getStockQuantity());
        }
    }

}