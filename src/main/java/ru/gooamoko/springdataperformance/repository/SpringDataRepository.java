package ru.gooamoko.springdataperformance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.gooamoko.springdataperformance.entity.MessageEntity;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Репозиторий на основе Spring Data.
 * Большая часть кода будет создана спрингом, остается только добавить один метод на основе запроса.
 */
public interface SpringDataRepository extends JpaRepository<MessageEntity, UUID> {

    @Transactional(readOnly = true)
    @Query("select m from MessageEntity m where m.id in (:ids)")
    List<MessageEntity> getEntitiesById(@Param("ids") Collection<UUID> identifiers);

    @Modifying
    @Transactional
    @Query(value = "truncate table messages", nativeQuery = true)
    void truncateTable();
}
