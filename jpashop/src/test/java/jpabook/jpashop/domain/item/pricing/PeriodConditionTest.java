package jpabook.jpashop.domain.item.pricing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PeriodConditionTest {

    @Test
    @DisplayName("시작시간이 종료시간보다 늦은 경우 예외가 발생한다.")
    void startTimeAfterEndTime() {
        // given
        var dayOfWeek = java.time.DayOfWeek.MONDAY;
        var startTime = java.time.LocalTime.of(10, 0);
        var endTime = java.time.LocalTime.of(9, 0);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> new PeriodCondition(dayOfWeek, startTime, endTime));
    }

    @Test
    @DisplayName("시작시간이 종료시간보다 이전인 경우 정상적으로 PeriodCondition이 생성된다.")
    void startTimeBeforeEndTime() {
        // given
        var dayOfWeek = java.time.DayOfWeek.MONDAY;
        var startTime = java.time.LocalTime.of(9, 0);
        var endTime = java.time.LocalTime.of(10, 0);

        // when
        var periodCondition = new PeriodCondition(dayOfWeek, startTime, endTime);

        // then
        assertNotNull(periodCondition);
        assertEquals(dayOfWeek, periodCondition.getDayOfWeek());
        assertEquals(startTime, periodCondition.getStartTime());
        assertEquals(endTime, periodCondition.getEndTime());
    }
}