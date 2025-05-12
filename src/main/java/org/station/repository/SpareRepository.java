package org.station.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.station.entity.Spare;

import java.util.List;

public interface SpareRepository extends JpaRepository<Spare, Long> {

    // 1. JOIN + SORT: Запчастини з їх типами ремонту, відсортовані за назвою
    @Query("SELECT s FROM Spare s LEFT JOIN s.repairType rt ORDER BY s.name ASC")
    List<Spare> findAllSortedByNameWithType();

    // 2. LIKE: Запчастини, назва яких починається з певного префікса
    @Query("SELECT s FROM Spare s WHERE s.name LIKE :prefix%")
    List<Spare> findByNameStartsWith(String prefix);

    // 3. BETWEEN: Кількість в діапазоні
    @Query("SELECT s FROM Spare s WHERE s.quantity BETWEEN :min AND :max")
    List<Spare> findByQuantityBetween(int min, int max);

    // 4. COUNT: Кількість усіх запчастин
    @Query("SELECT COUNT(s) FROM Spare s")
    long countAllSpares();

    // 5. GROUP BY: Кількість запчастин по кожному типу ремонту
    @Query("SELECT s.repairType.name, COUNT(s) FROM Spare s GROUP BY s.repairType.name")
    List<Object[]> countSparesByType();

    // 6. ALL: Запчастини з максимальною кількістю
    @Query("""
        SELECT s FROM Spare s
        WHERE s.quantity >= ALL (
            SELECT s2.quantity FROM Spare s2
        )
    """)
    List<Spare> findSparesWithMaxQuantity();

    // 7. Корельований підзапит: Запчастина з найбільшим об’ємом для кожного типу ремонту
    @Query("""
        SELECT s FROM Spare s
        WHERE s.quantity = (
            SELECT MAX(s2.quantity) FROM Spare s2 WHERE s2.repairType = s.repairType
        )
    """)
    List<Spare> findMaxQuantitySparePerType();

    // 9. UNION з коментарем
    @Query(value = """
        SELECT s.id, s.name, 'Використовується' AS comment
        FROM spares s
        WHERE EXISTS (SELECT 1 FROM spares_order o WHERE o.spare_id = s.id)
        UNION
        SELECT s.id, s.name, 'Не використовується' AS comment
        FROM spares s
        WHERE NOT EXISTS (SELECT 1 FROM spares_order o WHERE o.spare_id = s.id)
        """, nativeQuery = true)
    List<Object[]> getSparesWithComment();
}
