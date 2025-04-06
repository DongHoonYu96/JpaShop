package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.*;

@Entity //Member 테이블 만들어줘.
@Getter
@Setter
@NoArgsConstructor
public class Member {

    @Id //Long id를 키로 해줘.
    @GeneratedValue //기본키 자동으로 생성해줘(1씩증가)
    @Column(name = "member_id") //테이블 컬럼명 이걸로해줘. (기본값:변수이름)
    private Long id;

    @NotNull
    private String loginId;

    @NotNull
    private String password;

    @NotNull
    private String name;

    @Embedded //내장타입임.
    private Address address;

    @OneToMany(mappedBy = "member") // Member입장에서 Order는 일대다
    /*
    연관관계주인 : FK에 가까운 Order
    거울쪽에 mappedBy를 적어준다.
    나는 Order클래스의 member변수의 거울이다.
     */
    @JsonIgnore //api 요청시 반환안함!
    private List<Order> orders = new ArrayList<>();

    @Builder
    public Member(String name, Address address) {
        this.name = name;
        this.address = address;
    }
}
