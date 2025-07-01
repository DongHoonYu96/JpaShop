package jpabook.jpashop.domain.item.pricing;

import jpabook.jpashop.domain.item.DiscountCondition;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.time.*;

@Entity
@Getter
@DiscriminatorValue("PERIOD")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PeriodCondition extends DiscountCondition {
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    @Transient
    private Clock clock = Clock.system(ZoneId.of("Asia/Seoul"));

    public PeriodCondition(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, Clock clock) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("시작시간은 종료시간보다 이전이어야 합니다.");
        }
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.clock = clock;
    }

    public PeriodCondition(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("시작시간은 종료시간보다 이전이어야 합니다.");
        }
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.clock = Clock.system(ZoneId.of("Asia/Seoul"));
    }

    @Override
    public boolean isSatisfiedBy(Item item) {
        LocalDateTime now = LocalDateTime.now(clock);
        DayOfWeek today = now.getDayOfWeek();
        LocalTime currentTime = now.toLocalTime();
        return today.equals(dayOfWeek)
                && isStartTimeGreaterEqualThan(currentTime)
                && isEndTimeLessEqualThan(currentTime);
    }

    private boolean isStartTimeGreaterEqualThan(LocalTime currentTime) {
        return currentTime.equals(startTime) || currentTime.isAfter(startTime);
    }

    private boolean isEndTimeLessEqualThan(LocalTime currentTime) {
        return currentTime.equals(endTime) || currentTime.isBefore(endTime);
    }
}