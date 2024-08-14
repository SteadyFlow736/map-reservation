package org.example.mapreservation.hairshop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.mapreservation.common.Address;
import org.example.mapreservation.owner.domain.Owner;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class HairShop {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Embedded
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    // 경도
    private String longitude;
    // 위도
    private String latitude;

    public HairShop(String name, Address address, Owner owner, String longitude, String latitude) {
        this.name = name;
        this.address = address;
        this.owner = owner;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public HairShop(String name, Address address, Owner owner) {
        this.name = name;
        this.address = address;
        this.owner = owner;
        this.longitude = null;
        this.latitude = null;
    }

}
