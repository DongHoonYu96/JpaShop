package jpabook.jpashop.domain.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id @GeneratedValue
    @Column(name = "stock_id")
    private Long id;

    private String symbol; // 주식 종목 코드 (e.g., AAPL)

    private String name; // 회사 이름 (e.g., Apple Inc.)

    public Stock(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }
}
