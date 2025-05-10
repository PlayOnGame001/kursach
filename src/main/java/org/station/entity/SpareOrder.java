package org.station.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "spares_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpareOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "repair_id", referencedColumnName = "id")
    private Repair repair;

    @ManyToOne
    @JoinColumn(name = "spare_id", referencedColumnName = "id")
    private Spare spare;

    private int quantity;

    private double totalPrice;

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

}
