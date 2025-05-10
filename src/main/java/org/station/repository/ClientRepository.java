package org.station.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.station.entity.Client;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findClientsByProblem(String problem);
    List<Client> findClientsByCarBrand(String brand);
}

