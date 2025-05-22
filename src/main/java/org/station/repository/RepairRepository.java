package org.station.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.station.entity.Repair;

import java.time.LocalDate;
import java.util.List;

public interface RepairRepository extends JpaRepository<Repair, Long> {

    // 1. JOIN + сортування: Ремонти з майстрами, відсортовані за датою початку
    @Query("SELECT r FROM Repair r JOIN r.master m ORDER BY r.startDate ASC")
    List<Repair> findAllRepairsWithMastersSortedByStartDate();

    // 2. LIKE: Ремонти, де опис починається з певного слова
    @Query("SELECT r FROM Repair r WHERE r.description LIKE :prefix%")
    List<Repair> findRepairsByDescriptionPrefix(String prefix);

    // 3. BETWEEN: Ремонти між двома датами
    @Query("SELECT r FROM Repair r WHERE r.startDate BETWEEN :from AND :to")
    List<Repair> findRepairsInDateRange(LocalDate from, LocalDate to);

    // 4. COUNT усіх ремонтів
    @Query("SELECT COUNT(r) FROM Repair r")
    long countAllRepairs();

    // 5. COUNT з GROUP BY: Скільки ремонтів зробив кожен майстер
    @Query("SELECT r.master.fullName, COUNT(r) FROM Repair r GROUP BY r.master.fullName")
    List<Object[]> countRepairsByMaster();

    // 6. ANY: Ремонти, які почались раніше будь-якого з інших ремонтів
    @Query("SELECT r FROM Repair r WHERE r.startDate <= ALL (SELECT r2.startDate FROM Repair r2)")
    List<Repair> findEarliestRepairs();

    // 7. Корельований підзапит: Ремонт з найбільшою тривалістю для кожного авто
    @Query("""
        SELECT r FROM Repair r
        WHERE r.endDate - r.startDate = (
            SELECT MAX(r2.endDate - r2.startDate)
            FROM Repair r2
            WHERE r2.car.id = r.car.id
        )
        """)
    List<Repair> findLongestRepairPerCar();

    // 9. UNION з коментарем
    @Query(value = """
        SELECT r.id, 'Є запчастини' AS comment
        FROM repairs r
        WHERE EXISTS (SELECT 1 FROM spares_order o WHERE o.repair_id = r.id)
        UNION
        SELECT r.id, 'Без запчастин' AS comment
        FROM repairs r
        WHERE NOT EXISTS (SELECT 1 FROM spares_order o WHERE o.repair_id = r.id)
        """, nativeQuery = true)
    List<Object[]> getRepairCommentBySparePresence();

    @Query("SELECT r FROM Repair r JOIN FETCH r.repairType rt LEFT JOIN FETCH rt.spares")
    List<Repair> findAllWithRepairTypeAndSpares();

    @Query("SELECT r FROM Repair r JOIN FETCH r.car c JOIN FETCH r.master m ORDER BY r.startDate")
    List<Repair> findAllRepairsDetailed();

    @Query("SELECT r FROM Repair r WHERE r.master.id = :masterId")
    List<Repair> findRepairsByMasterId(@Param("masterId") Long masterId);

    @Query("SELECT r FROM Repair r WHERE r.car.id = :carId")
    List<Repair> findRepairsByCarId(@Param("carId") Long carId);

    @Query("SELECT r FROM Repair r " +
            "JOIN FETCH r.repairType rt " +
            "LEFT JOIN FETCH rt.spares " +
            "JOIN FETCH r.car c " +
            "JOIN FETCH r.master m " +
            "WHERE m.serviceStation.name = :stationName")
    List<Repair> findAllWithDetailsByStation(@Param("stationName") String stationName);


}
