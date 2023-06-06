package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository //component scan => 자동으로 스프링빈으로 등록해줘
@RequiredArgsConstructor
public class MemberRepository {

    //@PersistenceContext //EntityManager만들어서 주입해줘
    private final EntityManager em;

    public void save(Member member){
        em.persist(member); //JPA야 저장해줘.
    }

    public Member findOne(Long id){
        Member member = em.find(Member.class, id);  //(클래스, id(키))주면 알아서 JPA가 찾아줌
        return member;
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m",Member.class).getResultList();
                                                            //(쿼리,반환타입)를 리스트로만들어줘
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name= :name",Member.class)    //:name 은 파라미터바인딩
                .setParameter("name",name)
                .getResultList();
    }

}
