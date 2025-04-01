package jpabook.jpashop.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B")    //한테이블 전략(-) 구분X -> Discriminator로 구분
@Getter
@Setter
public class Book extends Item{
    private String author;
    private String isbn;
}
