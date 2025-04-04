package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class BookForm {

    private Long id;

    @NotBlank(message = "상품명은 필수입니다.")
    private String name;

    @Range(min = 0, max = 1000000)
    private int price;

    @Range(min = 0, max = 9999)
    private int stockQuantity;

    private String author;

    private String isbn;
}
