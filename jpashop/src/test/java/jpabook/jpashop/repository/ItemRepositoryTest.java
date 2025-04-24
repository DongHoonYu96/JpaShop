package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ActiveProfiles("local")
@SpringBootTest
//@Transactional
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("상품 저장 테스트")
    void save() {
        // given

        // when

        // then
    }

    @Test
    void findOne() {
        // given
        Book book = new Book();
        book.setName("JPA");
        book.setPrice(10000);
        book.setStockQuantity(10);
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
    }

    @Test
    void findAll() {

    }
}