package jpabook.jpashop.domain.common;;

import java.util.Objects;

public class Money {
    public static final Money ZERO = new Money(0);

    private int value;

    public Money(int value) {
        this.value = value;
    }

    public static Money of(int price) {
        return new Money(price);
    }

    public int getValue() {
        return value;
    }

    public Money plus(Money other) {
        return new Money(value + other.value);
    }

    public Money minus(Money other) {
        return new Money(value - other.value);
    }

    public Money multiply(int multiplier) {
        return new Money(value * multiplier);
    }

    public Money times(double multiplier) {
        return new Money((int) (value * multiplier));
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return value == money.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public boolean isLessThan(Money other) {
        return this.value < other.value;
    }
}
