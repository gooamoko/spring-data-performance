package ru.gooamoko.springdataperformance.service;

import ru.gooamoko.springdataperformance.entity.MessageEntity;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface MessageService {

    /**
     * Сохраняем сообщение в БД.
     *
     * @param entity сообщение.
     */
    void save(MessageEntity entity);

    /**
     * Удаляет все сообщения в БД.
     */
    void clear();

    /**
     * Получаем сообщения по их идентификаторам.
     *
     * @param identifiers коллекция идентификаторов сообщений.
     * @return список сообщений.
     */
    List<MessageEntity> getById(Collection<UUID> identifiers);
}
