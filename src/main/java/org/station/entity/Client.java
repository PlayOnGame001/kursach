package org.station.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clients")
@NoArgsConstructor
@Getter
@Setter
public class Client extends Person{

    @Column(name = "problem")
    private String problem;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "car_id")
    private Car car;

    public Client(String serviceStationName, String fullName, String phoneNumber, String problem, Car carId) {
        super(serviceStationName, fullName, phoneNumber);
        this.problem = problem;
        this.car = carId;
    }

    @Override
    public String toString() {
        return  serviceStationName +
                " " + fullName +
                " " + phoneNumber +
                " " + problem +
                " " + car;
    }
}

