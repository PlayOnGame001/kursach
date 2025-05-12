package org.station.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "car_brands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarBrand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    public CarBrand(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
