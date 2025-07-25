package jpabook.jpashop.controller.stock;

import jpabook.jpashop.domain.stock.Subscription;
import lombok.Data;

@Data
public class StockPriceDto {
    private Subscription subscription;
    private double currentPrice;
    private double ma20;

    public StockPriceDto(Subscription subscription, double currentPrice, double ma20) {
        this.subscription = subscription;
        this.currentPrice = currentPrice;
        this.ma20 = ma20;
    }
}
