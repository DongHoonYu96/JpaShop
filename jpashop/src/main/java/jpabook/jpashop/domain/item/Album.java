package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("A") //한테이블 전략(-) 구분X -> Discriminator로 구분
@Getter
@Setter
public class Album extends Item{
    private String artist;
    private String etc;
}
