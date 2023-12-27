package ru.gooamoko.springdataperformance.service;

import org.springframework.stereotype.Service;
import ru.gooamoko.springdataperformance.entity.MessageEntity;
import ru.gooamoko.springdataperformance.repository.EntityManagerRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы с сообщениями на основе Entity Manager.
 */
@Service
public class EntityManagerMessageService implements MessageService {
    private final EntityManagerRepository repository;

    public EntityManagerMessageService(EntityManagerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(MessageEntity entity) {
        repository.save(entity);
    }

    @Override
    public void clear() {
        repository.deleteAll();
    }

    @Override
    public List<MessageEntity> getById(Collection<UUID> identifiers) {
        return repository.getEntitiesById(identifiers);
    }
}
