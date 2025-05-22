package org.station.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "masters")
@NoArgsConstructor
@Getter
@Setter
public class Master extends Person {
    @Column(name = "address")
    private String address;

    @Column(name = "specialization")
    private String specialization;

    @ManyToOne
    @JoinColumn(name = "service_station_id")  // Убираем nullable=false
    private ServiceStation serviceStation;

    public Master(String serviceStationName, String fullName, String phoneNumber, String address, String specialization) {
        super(serviceStationName, fullName, phoneNumber);
        this.address = address;
        this.specialization = specialization;
        // serviceStation остается null
    }

    @Override
    public String toString() {
        return id +
                " " + fullName +
                " " + serviceStationName +
                " " + phoneNumber +
                " " + address +
                " " + specialization;
    }

    public Master(ServiceStation station, String fullName, String phoneNumber, String address, String specialization) {
        super(station != null ? station.getName() : "", fullName, phoneNumber);
        this.address = address;
        this.specialization = specialization;
        this.serviceStation = station;
    }
}