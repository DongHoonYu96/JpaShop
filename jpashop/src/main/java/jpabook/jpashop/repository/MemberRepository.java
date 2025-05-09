package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member){
        em.persist(member);
    }

    public Member findOne(Long id){
        Member member = em.find(Member.class, id);
        return member;
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m",Member.class).getResultList();
    }

    public List<Member> findByLoginId(String loginId){
        return em.createQuery("select m from Member m where m.loginId= :loginId",Member.class)    //:loginId 은 파라미터바인딩
                .setParameter("loginId",loginId)
                .getResultList();
    }

    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name= :name",Member.class)    //:name 은 파라미터바인딩
                .setParameter("name",name)
                .getResultList();
    }

}
