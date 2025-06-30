package jpabook.jpashop.repository;

import jpabook.jpashop.domain.common.Money;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.DiscountCondition;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.item.pricing.AmountDiscountPolicy;
import jpabook.jpashop.domain.item.pricing.NoneDiscountPolicy;
import jpabook.jpashop.domain.item.pricing.PeriodCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    void findOne() {
        // given
        Book book = Book.builder()
                .name("JPA")
                .price(10000)
                .stockQuantity(10)
                .author("김영한")
                .isbn("1234567890")
                .discountPolicy(new NoneDiscountPolicy())
                .build();
        itemRepository.save(book);

        // when
        Item findItem = itemRepository.findOne(book.getId());

        // then
        assertThat(findItem).extracting(
                "id",
                "name",
                "price",
                "stockQuantity"
        ).containsExactly(
                book.getId(),
                book.getName(),
                book.getPrice(),
                book.getStockQuantity()
        );
        assertThat(findItem.getDiscountPolicy()).isInstanceOf(NoneDiscountPolicy.class);
    }

    @Test
    @DisplayName("PeriodCondition이 포함된 AmountDiscountPolicy가 올바르게 반환되어야 한다")
    void findOneWithPeriodCondition() {
        // given
        AmountDiscountPolicy policy = new AmountDiscountPolicy(
                Money.of(1000),
                new PeriodCondition(
                        DayOfWeek.FRIDAY,
                        LocalTime.of(0, 0),
                        LocalTime.of(23, 59)
                )
        );
        Book book = new Book("오브젝트", 10000, 10, "조영호", "123456", policy);
        itemRepository.save(book);

        // when
        Book found = (Book) itemRepository.findOne(book.getId());

        // then
        // 1) 기본 필드 검증
        assertThat(found).extracting(
                "id", "name", "price", "stockQuantity", "author", "isbn"
        ).containsExactly(
                book.getId(),
                book.getName(),
                book.getPrice(),
                book.getStockQuantity(),
                book.getAuthor(),
                book.getIsbn()
        );

        // 2) 정책 타입 검증
        assertThat(found.getDiscountPolicy())
                .isInstanceOf(AmountDiscountPolicy.class);
        assertThat(found.getDiscountPolicy().getConditions().get(0))
                .isInstanceOf(PeriodCondition.class);

        List<DiscountCondition> conditions = found.getDiscountPolicy().getConditions();

        assertThat(conditions).hasSize(1);

        DiscountCondition cond = conditions.get(0);
        assertThat(cond).isInstanceOf(PeriodCondition.class);

        PeriodCondition pc = (PeriodCondition) cond;
        assertThat(pc.getDayOfWeek()).isEqualTo(DayOfWeek.FRIDAY);
        assertThat(pc.getStartTime()).isEqualTo(LocalTime.of(0, 0));
        assertThat(pc.getEndTime()).isEqualTo(LocalTime.of(23, 59));
    }
}