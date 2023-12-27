package ru.gooamoko.springdataperformance.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.gooamoko.springdataperformance.entity.MessageEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Самая низкоуровневая реализация - посредством JDBC.
 */
@Repository
public class JdbcMessageRepository {
    private final NamedParameterJdbcTemplate template;


    public JdbcMessageRepository(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    @Transactional
    public void save(MessageEntity entity) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", entity.getId());
        params.addValue("phone", entity.getPhone());
        params.addValue("msg", entity.getMessage());
        params.addValue("ts", entity.getCreateTimestamp());

        template.update("insert into messages(msg_id, msg_phone, msg_message, msg_create_ts) " +
                "values(:id, :phone, :msg, :ts)", params);
    }

    @Transactional
    public void truncateTable() {
        template.update("truncate table messages", new HashMap<>());
    }

    @Transactional(readOnly = true)
    public List<MessageEntity> getEntitiesById(Collection<UUID> identifiers) {
        return template.query("select * from messages where msg_id in (:ids)",
                new MapSqlParameterSource("ids", identifiers),
                (rs, rowNum) -> {
                    MessageEntity entity = new MessageEntity();
                    entity.setId(rs.getObject("msg_id", UUID.class));
                    entity.setPhone(rs.getString("msg_phone"));
                    entity.setMessage(rs.getString("msg_message"));
                    entity.setCreateTimestamp(rs.getTimestamp("msg_create_ts").toLocalDateTime());
                    return entity;
                });
    }

    @Transactional
    public void saveAll(Collection<MessageEntity> entities) {
        MapSqlParameterSource[] paramsArray = new MapSqlParameterSource[entities.size()];

        int index = 0;
        for (MessageEntity entity : entities) {
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("id", entity.getId());
            params.addValue("phone", entity.getPhone());
            params.addValue("msg", entity.getMessage());
            params.addValue("ts", entity.getCreateTimestamp());
            paramsArray[index++] = params;
        }

        template.batchUpdate("insert into messages(msg_id, msg_phone, msg_message, msg_create_ts) " +
                "values(:id, :phone, :msg, :ts)", paramsArray);
    }
}
