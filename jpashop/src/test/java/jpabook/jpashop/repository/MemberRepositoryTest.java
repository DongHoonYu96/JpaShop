package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    void findOne() {
        //given
        Member member1 = Member.builder()
                .name("member1")
                .address(new Address("seoul", "gannam", "123456"))
                .build();
        memberRepository.save(member1);

        //when
        Member findMember = memberRepository.findOne(member1.getId());

        //then
        assertThat(findMember.getName()).isEqualTo(member1.getName());
    }

    @Test
    void findAll() {
        //given
        Member member1 = Member.builder()
                .name("member1")
                .address(new Address("seoul", "gannam", "123456"))
                .build();

        Member member2 = Member.builder()
                .name("member2")
                .address(new Address("busan", "mafo", "456789"))
                .build();

        Member member3 = Member.builder()
                .name("member3")
                .address(new Address("gwangju", "suwan", "987654"))
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        //when
        List<Member> findMembers = memberRepository.findAll();

        //then
        assertThat(findMembers).hasSize(3)
            .extracting("name", "address")
            .containsExactlyInAnyOrder(
                    tuple("member1", new Address("seoul", "gannam", "123456")),
                    tuple("member2", new Address("busan", "mafo", "456789")),
                    tuple("member3", new Address("gwangju", "suwan", "987654"))
            );
    }
}