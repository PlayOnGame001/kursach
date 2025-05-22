package org.station.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.station.entity.ServiceStation;

import java.util.List;
import java.util.Optional;

public interface ServiceStationRepository extends JpaRepository<ServiceStation, Long> {

    // 1. JOIN + SORT: Список станцій, сортування за назвою
    @Query("SELECT s FROM ServiceStation s ORDER BY s.name ASC")
    List<ServiceStation> findAllSortedByName();

    // 2. LIKE: Пошук станцій, назва яких починається з ...
    @Query("SELECT s FROM ServiceStation s WHERE s.name LIKE :prefix%")
    List<ServiceStation> findByNameStartingWith(String prefix);

    // 3. BETWEEN: Назви станцій між двома значеннями
    @Query("SELECT s FROM ServiceStation s WHERE s.name BETWEEN :start AND :end")
    List<ServiceStation> findByNameBetween(String start, String end);

    // 4. COUNT: Загальна кількість станцій
    @Query("SELECT COUNT(s) FROM ServiceStation s")
    long countAllStations();

    // 5. GROUP BY: Кількість станцій за адресами
    @Query("SELECT s.address, COUNT(s) FROM ServiceStation s GROUP BY s.address")
    List<Object[]> countStationsPerAddress();

    // 6. ALL: Станції з найменшим ID (як приклад)
    @Query("""
        SELECT s FROM ServiceStation s
        WHERE s.id <= ALL (
            SELECT s2.id FROM ServiceStation s2
        )
    """)
    List<ServiceStation> findStationsWithMinId();

    // 7. Корельований підзапит: Станція з найбільшим ID (по суті: "найпізніше створена")
    @Query("""
        SELECT s FROM ServiceStation s
        WHERE s.id = (
            SELECT MAX(s2.id) FROM ServiceStation s2
        )
    """)
    List<ServiceStation> findNewestStation();

    // 8. NOT EXISTS — Станції, які не мають телефонів (можливо null)
    @Query("SELECT s FROM ServiceStation s WHERE s.phone IS NULL")
    List<ServiceStation> findStationsWithoutPhone();

    @Query("SELECT s FROM ServiceStation s WHERE s.id NOT IN (SELECT s2.id FROM ServiceStation s2 WHERE s2.phone IS NOT NULL)")
    List<ServiceStation> findStationsWithoutPhone_IN();

    @Query("SELECT s FROM ServiceStation s WHERE NOT EXISTS (SELECT 1 FROM ServiceStation s2 WHERE s2.id = s.id AND s2.phone IS NOT NULL)")
    List<ServiceStation> findStationsWithoutPhone_EXISTS();

    // 9. UNION з коментарем
    @Query(value = """
        SELECT s.id, s.name, 'Має телефон' AS comment
        FROM service_stations s
        WHERE s.phone IS NOT NULL
        UNION
        SELECT s.id, s.name, 'Телефон відсутній' AS comment
        FROM service_stations s
        WHERE s.phone IS NULL
        """, nativeQuery = true)
    List<Object[]> getStationsWithPhoneComment();

    @Query("SELECT s FROM ServiceStation s WHERE s.name = :name")
    Optional<ServiceStation> findByName(@Param("name") String name);

    @Query("SELECT s FROM ServiceStation s WHERE s.address LIKE %:address%")
    List<ServiceStation> findByAddressContaining(@Param("address") String address);


}
