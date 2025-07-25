package jpabook.jpashop.repository.stock;

import jpabook.jpashop.domain.stock.Stock;
import jpabook.jpashop.domain.stock.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByStock(Stock stock);
    List<Subscription> findByMemberId(Long memberId);
}
