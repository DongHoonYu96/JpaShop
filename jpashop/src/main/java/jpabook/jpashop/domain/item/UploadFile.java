package jpabook.jpashop.domain.item;

import lombok.*;

import javax.persistence.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UploadFile {
    private String uploadFileName;
    private String storeFileName;
}
