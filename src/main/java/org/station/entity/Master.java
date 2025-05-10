package org.station.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "masters")
@NoArgsConstructor
@Getter
@Setter
public class Master extends Person{
    @Column(name = "address")
    private String address;
    @Column(name = "specialization")
    private String specialization;

    public Master(String serviceStationName, String fullName, String phoneNumber, String address, String specialization) {
        super(serviceStationName, fullName, phoneNumber);
        this.address = address;
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return  id +
                " " + fullName +
                " " + serviceStationName +
                " " + phoneNumber +
                " " + address +
                " " + specialization;
    }
}

