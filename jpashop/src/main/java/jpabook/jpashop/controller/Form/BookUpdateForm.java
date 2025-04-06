package jpabook.jpashop.controller.Form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class BookUpdateForm {

    @NotNull(message = "상품 ID는 필수입니다.")
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
