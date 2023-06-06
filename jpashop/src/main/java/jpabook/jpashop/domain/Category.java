package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name="category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name="category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))    //다대다는 중간테이블필요.
    private List<Item> items=new ArrayList<>();

    //부모는 나랑같은타입, 1개
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    private Category parent;

    //자식은 여러개가능
    @OneToMany(mappedBy = "parent")
    private List<Category> child=new ArrayList<>();

    //연관편의메서드
    public void addChildCategory(Category child){
        this.child.add(child);
        child.setParent(this);
    }
}
