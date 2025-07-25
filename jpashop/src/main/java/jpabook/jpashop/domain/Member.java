package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import jpabook.jpashop.domain.stock.Subscription;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotNull
    private String loginId;

    @NotNull
    private String password;

    @NotNull
    private String name;

    private String email;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    @JsonIgnore
    private List<Subscription> subscriptions = new ArrayList<>();

    @Builder
    public Member(String name, Address address, String loginId, String password, String email) {
        this.name = name;
        this.address = address;
        this.loginId = loginId;
        this.password = password;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", loginId='" + loginId + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", address=" + address +
                '}';
    }
}
