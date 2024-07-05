package org.example.mapreservation.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Address {

    @Column(nullable = false)
    private String roadAddress;

    @Column(nullable = false)
    private String detailAddress;

    public Address(String roadAddress, String detailAddress) {
        this.roadAddress = roadAddress;
        this.detailAddress = detailAddress;
    }
}
