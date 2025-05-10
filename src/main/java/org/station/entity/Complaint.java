package org.station.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "complaints")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    private Client client;

    @ManyToOne
    private Master master;

    private String status;
}
