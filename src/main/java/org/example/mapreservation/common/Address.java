package org.example.mapreservation.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

    @Column(nullable = false)
    private String roadAddress;

    @Column(nullable = false)
    private String detailAddress;
}
