package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.item.pricing.NoneDiscountPolicy;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B")    //한테이블 전략(-) 구분X -> Discriminator로 구분
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Book extends Item {
    private String author;
    private String isbn;

    @Builder
    public Book(String name, int price, int stockQuantity, String author, String isbn, DiscountPolicy discountPolicy) {
        super(name, price, stockQuantity, discountPolicy);
        this.author = author;
        this.isbn = isbn;
        this.setDiscountPolicy(discountPolicy);
    }

    public Book(String name, int price, int stockQuantity, String author, String isbn) {
        super(name, price, stockQuantity, new NoneDiscountPolicy());
        this.author = author;
        this.isbn = isbn;
    }

}
