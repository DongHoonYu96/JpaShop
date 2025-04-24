package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true) //읽기전용 트랜잭션
public class ItemRepository {

    private final EntityManager em;

    @Transactional
    public void save(Item item){
        if(item.getId()==null){
            em.persist(item);   //아이디가없으면(새로생성된객체), 아이템 저장
        }
        else{
            em.merge(item); //이미DB에 등록된객체 -> 업데이트
        }
    }

    @Transactional(readOnly = false)
    public Item findOne(Long id){
        return em.find(Item.class, id, LockModeType.PESSIMISTIC_WRITE);
    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i",Item.class)
                .getResultList();
    }
}
