package ru.gooamoko.springdataperformance.service;

import org.springframework.stereotype.Service;
import ru.gooamoko.springdataperformance.entity.MessageEntity;
import ru.gooamoko.springdataperformance.repository.JdbcMessageRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class JdbcMessageService implements MessageService {
    private final JdbcMessageRepository repository;

    public JdbcMessageService(JdbcMessageRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(MessageEntity entity) {
        repository.save(entity);
    }

    @Override
    public void saveBatch(Collection<MessageEntity> entities) {
        repository.saveAll(entities);
    }

    @Override
    public void clear() {
        repository.truncateTable();
    }

    @Override
    public List<MessageEntity> getById(Collection<UUID> identifiers) {
        return repository.getEntitiesById(identifiers);
    }
}
