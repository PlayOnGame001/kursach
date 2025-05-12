package org.station.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.station.entity.Car;
import org.station.entity.Repair;
import org.station.entity.Master;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("SELECT r FROM Repair r JOIN r.car c ORDER BY r.startDate")
    List<Repair> findAllRepairsWithCarSortedByStartDate();

    @Query("SELECT c FROM Car c WHERE c.brand LIKE :prefix%")
    List<Car> findByBrandStartingWith(@Param("prefix") String prefix);

    @Query("SELECT r FROM Repair r WHERE r.startDate BETWEEN :start AND :end")
    List<Repair> findRepairsBetweenDates(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COUNT(c) FROM Car c")
    long countAllCars();

    @Query("SELECT c.brand, AVG(c.repairDuration) FROM Car c GROUP BY c.brand")
    List<Object[]> averageRepairDurationByBrand();

    @Query("SELECT c FROM Car c WHERE c.repairDuration >= ALL (SELECT c2.repairDuration FROM Car c2)")
    List<Car> findCarsWithMaxRepairDuration();

    @Query("SELECT r FROM Repair r WHERE r.startDate = (SELECT MAX(r2.startDate) FROM Repair r2 WHERE r2.master = r.master)")
    List<Repair> findLatestRepairPerMaster();

    @Query("SELECT m FROM Master m LEFT JOIN Repair r ON m = r.master AND r.startDate BETWEEN :start AND :end WHERE r.id IS NULL")
    List<Master> findMastersWithNoRepairsThisWeek_LeftJoin(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT m FROM Master m WHERE m.id NOT IN (SELECT r.master.id FROM Repair r WHERE r.startDate BETWEEN :start AND :end)")
    List<Master> findMastersWithNoRepairsThisWeek_In(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT m FROM Master m WHERE NOT EXISTS (SELECT r FROM Repair r WHERE r.master = m AND r.startDate BETWEEN :start AND :end)")
    List<Master> findMastersWithNoRepairsThisWeek_Exists(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query(value = """
        SELECT id, full_name, 'Має ремонти' AS comment
        FROM masters
        WHERE id IN (SELECT master_id FROM repairs)
        UNION
        SELECT id, full_name, 'Не має ремонти' AS comment
        FROM masters
        WHERE id NOT IN (SELECT master_id FROM repairs)
        """, nativeQuery = true)
    List<Object[]> getMastersWithRepairStatus();

    List<Car> getCarsByBrand(String brand);
}
