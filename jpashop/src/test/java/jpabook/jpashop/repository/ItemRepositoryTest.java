package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Album;
import jpabook.jpashop.domain.item.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("test")
@SpringBootTest
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

    }

    @Test
    void findAll() {

    }
}