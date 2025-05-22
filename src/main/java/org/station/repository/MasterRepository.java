package org.station.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.station.entity.Master;

import java.util.List;

public interface MasterRepository extends JpaRepository<Master, Long> {

    // Новый метод для безопасной загрузки всех мастеров без JOIN с ServiceStation
    @Query("SELECT m FROM Master m")
    List<Master> findAllMastersWithoutJoin();

    // Новый метод для безопасной загрузки всех мастеров с LEFT JOIN
    // (это даст null для несуществующих станций вместо ошибки)
    @Query("SELECT m FROM Master m LEFT JOIN m.serviceStation s")
    List<Master> findAllMastersWithLeftJoin();

    // Оригинальные методы ниже...

    // 1. JOIN + сортировка: Майстри з відображенням їх спеціалізації, відсортовані за СТО
    @Query("SELECT m FROM Master m ORDER BY m.serviceStationName ASC")
    List<Master> findAllMastersSortedByStation();

    // 2. LIKE: Майстри, чиє ім'я починається на певну літеру
    @Query("SELECT m FROM Master m WHERE m.fullName LIKE :prefix%")
    List<Master> findByNameStartsWith(String prefix);

    // 3. BETWEEN: Майстри, які спеціалізуються на категоріях від A до Z
    @Query("SELECT m FROM Master m WHERE m.specialization BETWEEN :from AND :to")
    List<Master> findMastersBySpecializationRange(String from, String to);

    // 4. Aggregation без групування: Кількість усіх майстрів
    @Query("SELECT COUNT(m) FROM Master m")
    long countAllMasters();

    // 5. Aggregation з групуванням: Скільки майстрів працює на кожній СТО
    @Query("SELECT m.serviceStationName, COUNT(m) FROM Master m GROUP BY m.serviceStationName")
    List<Object[]> countMastersByStation();

    // 6. ALL: Майстри, які працюють у найбільш представленій СТО
    @Query("""
        SELECT m FROM Master m 
        WHERE m.serviceStationName = ALL (
            SELECT m2.serviceStationName 
            FROM Master m2 
            GROUP BY m2.serviceStationName 
            ORDER BY COUNT(m2) DESC
            LIMIT 1
        )
        """)
    List<Master> findMastersFromLargestStation();

    // 7. Корельований підзапит: Майстри зі спеціалізацією з найбільшим числом майстрів
    @Query("""
        SELECT m FROM Master m
        WHERE m.specialization = (
            SELECT m2.specialization
            FROM Master m2
            GROUP BY m2.specialization
            ORDER BY COUNT(m2) DESC
            LIMIT 1
        )
        """)
    List<Master> findTopSpecializedMasters();

    // 8. NOT EXISTS: Майстри без спеціалізації (усі 3 варіанти)
    @Query("SELECT m FROM Master m LEFT JOIN Repair r ON r.master.id = m.id WHERE r.id IS NULL")
    List<Master> findMastersWithoutRepairs_LeftJoin();

    @Query("SELECT m FROM Master m WHERE m.id NOT IN (SELECT r.master.id FROM Repair r)")
    List<Master> findMastersWithoutRepairs_IN();

    @Query("SELECT m FROM Master m WHERE NOT EXISTS (SELECT 1 FROM Repair r WHERE r.master.id = m.id)")
    List<Master> findMastersWithoutRepairs_Exists();

    // 9. UNION з коментарем (native)
    @Query(value = """
        SELECT m.id, m.full_name, 'Працює' AS comment
        FROM masters m
        WHERE m.specialization IS NOT NULL
        UNION
        SELECT m.id, m.full_name, 'Без спеціалізації' AS comment
        FROM masters m
        WHERE m.specialization IS NULL
        """, nativeQuery = true)
    List<Object[]> getMastersWithComment();

    @Query("SELECT m FROM Master m WHERE LOWER(m.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Master> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT m FROM Master m WHERE m.serviceStationName = :station")
    List<Master> findAllByServiceStation(@Param("station") String serviceStationName);
}