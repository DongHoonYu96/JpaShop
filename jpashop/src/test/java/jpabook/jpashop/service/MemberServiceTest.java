package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)    //JUnit 실행할때 스프링이랑 같이 실행해줘
@SpringBootTest //Spring boot 띄운상태로 테스트 돌려줘. (없으면 Autowired 안됨)
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception{
        //given
        Member member=new Member();
        member.setName("kim");

        //when
        Long savedId= memberService.join(member);

        //then
        assertEquals(member,memberRepository.findOne(savedId)); //멤버랑 찾은얘랑 같은가? ㅇㅇ 같은 영속성에서 pk가 같으면 같음.
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception{
        //given
        Member member1=new Member();
        member1.setName("kim");

        Member member2=new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        memberService.join(member2);    //예외가 발생해야한다.

        //then
        fail("예외가 발생해야 한다.");   //여기줄까지 오면안됨
    }
}