package org.station.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.station.entity.RepairType;

import java.util.List;

public interface RepairTypeRepository extends JpaRepository<RepairType, Long> {

    // 1. JOIN + SORT: Типи ремонту з кількістю ремонтів, відсортовано за назвою
    @Query("SELECT rt FROM RepairType rt LEFT JOIN rt.repairs r GROUP BY rt.id ORDER BY rt.name ASC")
    List<RepairType> findAllSortedByNameWithRepairCount();

    // 2. LIKE: Типи ремонту, назва яких починається на вказану літеру
    @Query("SELECT rt FROM RepairType rt WHERE rt.name LIKE :prefix%")
    List<RepairType> findByNameStartsWith(String prefix);

    // 3. BETWEEN: Типи ремонту з назвами між двома значеннями
    @Query("SELECT rt FROM RepairType rt WHERE rt.name BETWEEN :from AND :to")
    List<RepairType> findByNameBetween(String from, String to);

    // 4. COUNT: Загальна кількість типів ремонту
    @Query("SELECT COUNT(rt) FROM RepairType rt")
    long countAllTypes();

    // 5. GROUP BY: Кількість ремонтів по кожному типу
    @Query("SELECT rt.name, COUNT(r) FROM RepairType rt LEFT JOIN rt.repairs r GROUP BY rt.name")
    List<Object[]> countRepairsPerType();

    // 6. ALL: Тип(и) ремонту з найбільшою кількістю ремонтів
    @Query("""
        SELECT rt FROM RepairType rt
        WHERE SIZE(rt.repairs) >= ALL (
            SELECT SIZE(r2.repairs) FROM RepairType r2
        )
    """)
    List<RepairType> findMostUsedRepairType();

    // 7. Корельований підзапит: Типи ремонту з найбільшою кількістю запчастин
    @Query("""
        SELECT rt FROM RepairType rt
        WHERE SIZE(rt.spares) = (
            SELECT MAX(SIZE(r2.spares)) FROM RepairType r2
        )
    """)
    List<RepairType> findRepairTypeWithMostSpares();

    // 8. NOT EXISTS: Типи ремонту без ремонтів
    @Query("SELECT rt FROM RepairType rt LEFT JOIN rt.repairs r WHERE r IS NULL")
    List<RepairType> findRepairTypesWithoutRepairs_LeftJoin();

    @Query("SELECT rt FROM RepairType rt WHERE rt.id NOT IN (SELECT DISTINCT r.repairType.id FROM Repair r)")
    List<RepairType> findRepairTypesWithoutRepairs_IN();

    @Query("SELECT rt FROM RepairType rt WHERE NOT EXISTS (SELECT 1 FROM Repair r WHERE r.repairType = rt)")
    List<RepairType> findRepairTypesWithoutRepairs_Exists();

    // 9. UNION з коментарем
    @Query(value = """
        SELECT rt.id, rt.name, 'Є ремонти' AS comment
        FROM repair_types rt
        WHERE EXISTS (SELECT 1 FROM repairs r WHERE r.repair_type_id = rt.id)
        UNION
        SELECT rt.id, rt.name, 'Немає ремонтів' AS comment
        FROM repair_types rt
        WHERE NOT EXISTS (SELECT 1 FROM repairs r WHERE r.repair_type_id = rt.id)
        """, nativeQuery = true)
    List<Object[]> getRepairTypesWithComment();
}
