package org.station.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cars")
@NoArgsConstructor
@Getter
@Setter
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private long id;

    @Column(name = "brand")
    private String brand;

    @Column(name = "release_year")
    private int releaseYear;

    @Column(name = "price")
    private double price;

    @Column(name = "repair_duration")
    private int repairDuration;

    public Car(String brand, int releaseYear, double price, int repairDuration) {
        this.brand = brand;
        this.releaseYear = releaseYear;
        this.price = price;
        this.repairDuration = repairDuration;
    }

    @Override
    public String toString() {
        return  id +
                " " + brand +
                " " + releaseYear +
                " " + price +
                " " + repairDuration;
    }
}

