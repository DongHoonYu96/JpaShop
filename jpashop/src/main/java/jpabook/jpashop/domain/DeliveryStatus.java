package jpabook.jpashop.domain;

public enum DeliveryStatus {
    READY("배송준비"), SHIPPING("배송중"),  COMPLETE("배송완료");

    private final String description;

    private DeliveryStatus(String description) {
        this.description = description;
    }
}
