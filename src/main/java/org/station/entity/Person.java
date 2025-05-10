package org.station.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
public abstract class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "service_station_name")
    protected String serviceStationName;

    @Column(name = "full_name")
    protected String fullName;

    @Column(name = "phone_number")
    protected String phoneNumber;

    public Person(String serviceStationName, String fullName, String         phoneNumber) {
        this.serviceStationName = serviceStationName;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }
}

