package jpabook.jpashop.controller.Form;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

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

    private List<MultipartFile> imageFiles;

    private String author;

    private String isbn;
}
