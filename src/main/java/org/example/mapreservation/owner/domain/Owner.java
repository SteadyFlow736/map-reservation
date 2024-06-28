package org.example.mapreservation.owner.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Owner {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;
}
