package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)   //상속전략 : 한 테이블에 다때려박기
@DiscriminatorColumn(name="dtype")  //자식구분자
@Getter @Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name="item_id")
    private Long id;

    private  String name;
    private  int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직==//

    /**
     * 재고증가
     * @param quantity
     */
    public void addStock(int quantity){
        this.stockQuantity+=quantity;
    }

    /**
     * 재고 줄이기
     * @param quatity
     */
    public void removeStock(int quatity){
        int restStock = this.stockQuantity - quatity;
        if(restStock<0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity=restStock;
    }
}
