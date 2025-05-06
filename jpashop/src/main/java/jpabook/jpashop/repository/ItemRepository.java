package jpabook.jpashop.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.Collections;
import java.util.List;

import static jpabook.jpashop.domain.item.QItem.item;

@Repository
@Transactional(readOnly = true) //읽기전용 트랜잭션
public class ItemRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public ItemRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

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

    public List<Item> findAllByQueryDsl(Long itemId, int pageSize) {
        return query
                .select(item)
                .from(item)
                .where(ltBookId(itemId))
                .orderBy(item.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression ltBookId(Long itemId) {
        if (itemId == null) {
            return null;
        }
        return item.id.lt(itemId);
    }

    public List<Item> findAllByQueryDsl(int pageNum) {
        return query
                .select(item)
                .from(item)
                .orderBy(item.id.desc())
                .offset(pageNum * 10)
                .limit(10)
                .fetch();
    }

    public List<Item> findAllByQueryDslWithIdsAndInClause(int pageNum) {
        // 1. ID 목록만 먼저 조회
        List<Long> itemIds = query
                .select(item.id)
                .from(item)
                .orderBy(item.id.desc())
                .offset(pageNum * 10)
                .limit(10)
                .fetch();

        // 2. ID 목록이 비어있으면 빈 리스트 반환
        if (itemIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. IN절을 사용하여 실제 데이터를 조회
        return query
                .select(item)
                .from(item)
                .where(item.id.in(itemIds))
                .orderBy(item.id.desc())
                .fetch();
    }

}
