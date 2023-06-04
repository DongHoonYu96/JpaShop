package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class) //jnunit에게 Spring관련테스트임을 알려줌
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Test
    /*
    문제 : EntityManager를 통한 data변경은 트랜잭션안에서 이뤄져야함
    해결 : @Transactional
     */
    @Transactional
    /*
    문제 : Test끝나면 data 롤백됨 => db에서 확인이안됨
    해결 : Rollback(false)
     */
    @Rollback(false)
    public  void testMember() throws Exception{
        //given
        Member member=new Member();
        member.setUsername("MemberA");


        //when
        Long savedID = memberRepository.save(member);//좌측에 변수자동넣기 C Alt V
        Member findMember = memberRepository.find(savedID);

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);
        //JPA 영속성 동일성 보장
        //같은 트랜잭션안에서 저장,조회 => 영속성 컨텍스트가 같음 => id가같으면 같은 Entity로 인식
        //=> 기존관리하던거 return
    }

}