package org.station.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "repair_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RepairType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "repairType", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Repair> repairs = new ArrayList<>();

    @OneToMany(mappedBy = "repairType", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Spare> spares = new ArrayList<>();
}
