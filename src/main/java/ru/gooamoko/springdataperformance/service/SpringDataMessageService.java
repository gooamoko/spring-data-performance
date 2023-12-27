package ru.gooamoko.springdataperformance.service;

import org.springframework.stereotype.Service;
import ru.gooamoko.springdataperformance.entity.MessageEntity;
import ru.gooamoko.springdataperformance.repository.SpringDataRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы с сообщениями, использующий репозиторий Spring Data.
 */
@Service
public class SpringDataMessageService implements MessageService {
    private final SpringDataRepository repository;

    public SpringDataMessageService(SpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(MessageEntity entity) {
        repository.save(entity);
    }

    @Override
    public void clear() {
        repository.deleteAll();
//        repository.truncateTable();
    }

    @Override
    public List<MessageEntity> getById(Collection<UUID> identifiers) {
        return repository.getEntitiesById(identifiers);
    }
}
