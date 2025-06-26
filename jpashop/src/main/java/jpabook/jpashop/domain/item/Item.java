package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.common.Money;
import jpabook.jpashop.domain.item.pricing.NoneDiscountPolicy;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="dtype")  //자식구분자
@Getter @Setter
@SuperBuilder
@NoArgsConstructor
public abstract class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "item_image",
            joinColumns = @JoinColumn(name = "item_id")
    )
    @OrderColumn(name = "list_idx")
    private List<UploadFile> images = new ArrayList<>();

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    @Transient
    private DiscountPolicy discountPolicy;

    protected Item(String name , int price, int stockQuantity, DiscountPolicy discountPolicy) {
        this.name=name;
        this.price=price;
        this.stockQuantity=stockQuantity;
        this.discountPolicy= discountPolicy;
    }

    protected Item(String name, int price, int stockQuantity) {
        this(name, price, stockQuantity, new NoneDiscountPolicy());
    }

    //==비즈니스 로직==//
    public void addStock(int quantity){
        this.stockQuantity+=quantity;
    }

    public void removeStock(int quatity){
        int restStock = this.stockQuantity - quatity;
        if(restStock<0){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity=restStock;
    }

    public Money calculateItemFee(Order order) {
        return Money.of(price).minus(discountPolicy.calculateDiscountAmount(order, this));
    }
}
