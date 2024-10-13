package org.example.mapreservation.hairshop.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.mapreservation.common.Address;
import org.example.mapreservation.owner.domain.Owner;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class HairShop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Convert(converter = UrlListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> imageUrls = new ArrayList<>();

    @Builder
    public HairShop(Long id, String name, Address address, Owner owner, String longitude, String latitude, List<String> imageUrls) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.owner = owner;
        this.longitude = longitude;
        this.latitude = latitude;
        this.imageUrls = imageUrls;
    }

    public HairShop(String name, Address address, Owner owner, String longitude, String latitude, List<String> imageUrls) {
        this.name = name;
        this.address = address;
        this.owner = owner;
        this.longitude = longitude;
        this.latitude = latitude;
        if (imageUrls != null) {
            this.imageUrls = imageUrls;
        }
    }

    public HairShop(String name, Address address, Owner owner, List<String> imageUrls) {
        this.name = name;
        this.address = address;
        this.owner = owner;
        this.longitude = null;
        this.latitude = null;
        if (imageUrls != null) {
            this.imageUrls = imageUrls;
        }
    }

    public HairShop(String name, Address address, Owner owner) {
        this.name = name;
        this.address = address;
        this.owner = owner;
        this.longitude = null;
        this.latitude = null;
    }

    public void updateLongitudeLatitude(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Converter
    static class UrlListConverter implements AttributeConverter<List<String>, String> {
        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(List<String> attribute) {
            try {
                return objectMapper.writeValueAsString(attribute);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public List<String> convertToEntityAttribute(String dbData) {
            try {
                return objectMapper.readValue(dbData, List.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

