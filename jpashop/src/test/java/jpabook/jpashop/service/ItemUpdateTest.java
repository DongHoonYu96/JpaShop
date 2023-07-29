package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception{

        Book book = em.find(Book.class, 1L);

        //트랜젝션안에서
        book.setName("asdfghf");
        //set이후 트랜잭션 커밋하면 스프링이 자동으로 DB에 반영해줌 == 변경감지

    }
}
