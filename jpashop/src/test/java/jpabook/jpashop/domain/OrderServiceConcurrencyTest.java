package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
//@Transactional
public class OrderServiceConcurrencyTest {
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
    @DisplayName("n명이 k개씩 주문하면 재고가 n*k개 줄어야 한다.")
    public void stock_decrease_concurrency() throws Exception {
        // given
        int stockQuantity = 100;
        TestDataDto testData = testDataService.createTestData(stockQuantity);
        Long bookId = testData.getBookId();
        Long memberId = testData.getMemberId();
        
        int orderCount = 5;
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when - 주문 요청
        for(int i=0; i<threadCount; i++){
            executorService.submit(() -> {
                try {
                    orderService.order(memberId, bookId, orderCount);
                } catch (Exception e) {
                    log.error("주문 실패 : {}", e.getMessage());
                }
                latch.countDown();
            });
        }
        latch.await();
        executorService.shutdown();

        // then - 최종 재고 확인
        testDataService.verifyFinalStock(bookId, stockQuantity - orderCount * threadCount);
    }
}

// 별도의 테스트 데이터 DTO
@Data
class TestDataDto {
    private Long bookId;
    private Long memberId;
}

// 테스트 데이터 관리용 서비스
@Service
@Slf4j
class TestDataService {
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
    
    @Transactional(readOnly = false)
    public void verifyFinalStock(Long itemId, int expectedStock) {
        em.clear(); // 영속성 컨텍스트 초기화
        Item item = itemRepository.findOne(itemId);
        log.info("최종 재고: {}", item.getStockQuantity());
        assertEquals(expectedStock, item.getStockQuantity());
    }
}