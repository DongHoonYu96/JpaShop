package jpabook.jpashop.repository.stock;

import jpabook.jpashop.domain.stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findBySymbol(String symbol);
    List<Stock> findByNameContaining(String name);
}
