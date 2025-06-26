package jpabook.jpashop.domain.item.pricing;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.DiscountCondition;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class PeriodCondition implements DiscountCondition {
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