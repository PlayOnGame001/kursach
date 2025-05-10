package org.station.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "repairs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Repair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;


    @ManyToOne
    @JoinColumn(name = "master_id")
    private Master master;

    @ManyToOne
    @JoinColumn(name = "repair_type_id")
    private RepairType repairType;


    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private String description;

    @OneToMany(mappedBy = "repair", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpareOrder> spareOrders = new ArrayList<>();

}
