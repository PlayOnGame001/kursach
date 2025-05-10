package org.station.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_stations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private String phone;

}
