package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
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

        assertThat(member).isEqualTo(memberRepository.findOne(savedId));
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception{
        //given
        Member member1=new Member();
        member1.setLoginId("kim");

        Member member2=new Member();
        member2.setLoginId("kim");

        //when
        memberService.join(member1);
        memberService.join(member2);    //예외가 발생해야한다.

        //then
        Assertions.fail("예외가 발생해야 한다.");
    }

    @Test
    public void 업데이트() {
        //given
        Member member = new Member();
        member.setName("kim");
        Long savedId = memberService.join(member);

        //when
        memberService.update(savedId, "lee");
        Member findMember = memberService.findOne(savedId);

        //then
        assertThat(findMember.getName()).isEqualTo("lee");
    }
}