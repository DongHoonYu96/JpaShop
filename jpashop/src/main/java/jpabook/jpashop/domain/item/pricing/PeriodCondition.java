package jpabook.jpashop.domain.item.pricing;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.DiscountCondition;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Getter
@DiscriminatorValue("PERIOD")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class PeriodCondition extends DiscountCondition {
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public PeriodCondition(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean isSatisfiedBy(Order order) {
        return order.getOrderDate().getDayOfWeek().equals(dayOfWeek) &&
                startTime.compareTo(order.getOrderDate().toLocalTime()) <= 0&&
                endTime.compareTo(order.getOrderDate().toLocalTime()) >= 0;
    }
}