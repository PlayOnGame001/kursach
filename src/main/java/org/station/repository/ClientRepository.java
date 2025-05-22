package org.station.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.station.entity.Client;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT c FROM Client c JOIN c.car car ORDER BY car.releaseYear")
    List<Client> findAllClientsWithCarsSortedByCarYear();

    @Query("SELECT c FROM Client c WHERE c.fullName LIKE :prefix%")
    List<Client> findClientsByNamePrefix(@Param("prefix") String prefix);

    @Query("SELECT c FROM Client c WHERE c.car.releaseYear BETWEEN :startYear AND :endYear")
    List<Client> findClientsByCarYearBetween(@Param("startYear") int start, @Param("endYear") int end);

    @Query("SELECT COUNT(c) FROM Client c")
    long countAllClients();

    @Query("SELECT c.car.brand, COUNT(c) FROM Client c GROUP BY c.car.brand")
    List<Object[]> countClientsByCarBrand();

    @Query("SELECT c FROM Client c WHERE c.car.releaseYear >= ALL (SELECT c2.car.releaseYear FROM Client c2)")
    List<Client> findClientsWithNewestCars();

    @Query("SELECT c FROM Client c WHERE c.car.price = (SELECT MAX(c2.car.price) FROM Client c2 WHERE c2.car.brand = c.car.brand)")
    List<Client> findClientWithMostExpensiveCarPerBrand();

    @Query("SELECT c FROM Client c LEFT JOIN c.car car WHERE car IS NULL")
    List<Client> findClientsWithoutCars_LeftJoin();

    @Query("SELECT c FROM Client c WHERE c.id NOT IN (SELECT c2.id FROM Client c2 WHERE c2.car IS NOT NULL)")
    List<Client> findClientsWithoutCars_In();

    @Query("SELECT c FROM Client c WHERE NOT EXISTS (SELECT 1 FROM Client c2 WHERE c2.id = c.id AND c2.car IS NOT NULL)")
    List<Client> findClientsWithoutCars_Exists();

    @Query(value = """
        SELECT c.id, c.full_name, 'Має авто' AS comment
        FROM clients c
        WHERE c.car_id IS NOT NULL
        UNION
        SELECT c.id, c.full_name, 'Не має авто' AS comment
        FROM clients c
        WHERE c.car_id IS NULL
        """, nativeQuery = true)
    List<Object[]> getClientsWithCarComment();

    @Query("SELECT c FROM Client c WHERE c.car.id = :carId")
    Optional<Client> findClientByCarId(@Param("carId") Long carId);

    @Query("SELECT c FROM Client c WHERE LOWER(c.problem) LIKE LOWER(CONCAT('%', :problem, '%'))")
    List<Client> findClientsByProblem(@Param("problem") String problem);

    @Query("SELECT cl FROM Client cl WHERE cl.car.brand = :brand")
    List<Client> findClientsByCarBrand(@Param("brand") String brand);

}
