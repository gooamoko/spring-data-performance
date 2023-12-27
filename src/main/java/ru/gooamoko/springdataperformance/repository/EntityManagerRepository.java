package ru.gooamoko.springdataperformance.repository;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.gooamoko.springdataperformance.entity.MessageEntity;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Репозиторий на основе EntityManager.
 * Это уже не Spring Data. Придется реализовать методы самостоятельно.
 */
@Repository
public class EntityManagerRepository {
    private final EntityManager entityManager;

    public EntityManagerRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void save(MessageEntity entity) {
        entityManager.persist(entity);
    }

    @Transactional
    public void saveAll(Collection<MessageEntity> entities) {
        for (MessageEntity entity : entities) {
            entityManager.persist(entity);
        }
    }

    @Transactional
    public void deleteAll() {
        entityManager.createNativeQuery("truncate table messages").executeUpdate();
    }

    @Transactional(readOnly = true)
    public List<MessageEntity> getEntitiesById(Collection<UUID> identifiers) {
        return entityManager
                .createQuery("select m from MessageEntity m where m.id in (:ids)", MessageEntity.class)
                .setParameter("ids", identifiers)
                .getResultList();
    }
}
