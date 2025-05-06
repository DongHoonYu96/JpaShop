package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.repository.ItemRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("local")
public class ItemServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("상품이름 수정시 변경된 값이 반영되어야 한다.")
    @Commit
    public void updateItemTest(){
        Book book = createItem();

        book.setName("asdfghf");
        itemService.updateItem(book.getId(), book.getName(), book.getPrice(), book.getStockQuantity());

        Book findBook = (Book) itemRepository.findOne(book.getId());
        assertThat(findBook.getName()).isEqualTo("asdfghf");
    }

    private Book createItem() {
        Book book = new Book();
        book.setName("JPA");
        book.setPrice(10000);
        book.setStockQuantity(10);
        em.persist(book);
        return book;
    }
}
