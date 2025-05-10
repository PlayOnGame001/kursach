package org.station.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "spares")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "repairType")
public class Spare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "repair_type_id")
    private RepairType repairType;

    public Spare(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }
}
