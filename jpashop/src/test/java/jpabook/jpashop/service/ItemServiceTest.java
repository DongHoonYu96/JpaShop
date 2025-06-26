package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.UploadFile;
import jpabook.jpashop.repository.ItemRepository;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ItemServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("상품이름 수정시 변경된 값이 반영되어야 한다.")
    public void updateItemTest() {
        Book book = createItem();

        book.setName("JPA Version 2");
        itemService.updateItem(book.getId(), book.getName(), book.getPrice(), book.getStockQuantity());

        Book findBook = (Book) itemRepository.findOne(book.getId());
        assertThat(findBook.getName()).isEqualTo("JPA Version 2");
    }

    @Test
    @DisplayName("Item에 이미지 컬렉션이 ElementCollection으로 저장되고 순서가 유지되어야 한다")
    public void testSaveAndLoadImages() {
        // given
        Book book = createItem();

        UploadFile img1 = new UploadFile("first.jpg", "uuid-first.jpg");
        UploadFile img2 = new UploadFile("second.jpg", "uuid-second.jpg");
        UploadFile img3 = new UploadFile("third.jpg", "uuid-third.jpg");

        // 이미지 순서대로 추가
        book.getImages().add(img1);
        book.getImages().add(img2);
        book.getImages().add(img3);

        // when
        itemRepository.save(book);
        Book found = (Book) itemRepository.findOne(book.getId());
        List<UploadFile> images = found.getImages();

        // then
        assertThat(images).hasSize(3)
                        .extracting("uploadFileName", "storeFileName")
                        .containsExactly(
                                tuple("first.jpg", "uuid-first.jpg"),
                                tuple("second.jpg", "uuid-second.jpg"),
                                tuple("third.jpg", "uuid-third.jpg")
                        );
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
