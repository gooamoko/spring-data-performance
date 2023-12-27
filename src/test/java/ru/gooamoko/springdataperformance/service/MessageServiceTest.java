package ru.gooamoko.springdataperformance.service;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.gooamoko.springdataperformance.entity.MessageEntity;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MessageServiceTest {
    private static final Logger log = LoggerFactory.getLogger(MessageServiceTest.class);
    private static final int COUNT = 500000;
    private static final int BATCH_SIZE = 1000;
    private static final String[] messages = new String[] {
      "Сегодня вам стоит избегать путешествий.",
      "Сегодня как никогда для вас будет важно внимание близкий людей.",
      "Сегодня воздержитесь от употребление алкоголя.",
      "Сегодня даже самое безобидное ваше высказывание по отношению к коллегам может перерасти в конфликт.",
      "Сегодня ваш счастливый день. Не бойтесь экспериментировать. Удача на вашей стороне.",
      "Сегодня вам нужно проявлять осторожность во всём. Особенно осторожно стоит отнестись к крупным покупкам.",
    };

    private final SpringDataMessageService springDataMessageService;
    private final EntityManagerMessageService entityManagerMessageService;
    private final JdbcMessageService jdbcMessageService;

    @Autowired
    public MessageServiceTest(SpringDataMessageService springDataMessageService,
                              EntityManagerMessageService entityManagerMessageService,
                              JdbcMessageService jdbcMessageService) {
        this.springDataMessageService = springDataMessageService;
        this.entityManagerMessageService = entityManagerMessageService;
        this.jdbcMessageService = jdbcMessageService;
    }


    @Test
    public void testSpringDataRepository() {
        log.info("Testing spring data repository.");
        long time = System.currentTimeMillis();
        doTest(springDataMessageService);
        log.info("Spring data takes {} ms.", System.currentTimeMillis() - time);
    }

    @Test
    public void testEntityManagerRepository() {
        log.info("Testing entity manager repository.");
        long time = System.currentTimeMillis();
        doTest(entityManagerMessageService);
        log.info("Entity manager takes {} ms.", System.currentTimeMillis() - time);
    }

    @Test
    public void testJdbcRepository() {
        log.info("Testing JDBC template repository.");
        long time = System.currentTimeMillis();
        doTest(jdbcMessageService);
        log.info("JDBC template takes {} ms.", System.currentTimeMillis() - time);
    }

    @Test
    public void testBatchSpringDataRepository() {
        log.info("Testing batch spring data repository with batch size {}.", BATCH_SIZE);
        long time = System.currentTimeMillis();
        doBatchTest(springDataMessageService);
        log.info("Batch spring data takes {} ms.", System.currentTimeMillis() - time);
    }

    @Test
    public void testBatchEntityManagerRepository() {
        log.info("Testing batch entity manager repository with batch size {}.", BATCH_SIZE);
        long time = System.currentTimeMillis();
        doBatchTest(entityManagerMessageService);
        log.info("Batch entity manager takes {} ms.", System.currentTimeMillis() - time);
    }

    @Test
    public void testBatchJdbcRepository() {
        log.info("Testing batch JDBC template repository with batch size {}.", BATCH_SIZE);
        long time = System.currentTimeMillis();
        doBatchTest(jdbcMessageService);
        log.info("Batch JDBC template takes {} ms.", System.currentTimeMillis() - time);
    }


    private void doTest(MessageService service) {
        // Удаляем все записи, если таковые имеются.
        long time = System.currentTimeMillis();
        service.clear();
        log.info("Delete records takes {} ms.", System.currentTimeMillis() - time);


        time = System.currentTimeMillis();
        Set<UUID> identifiers = new HashSet<>();
        int identifiersCount = COUNT / 5;
        Random random = new SecureRandom();
        // добавляем N записей
        for (int i = 0; i < COUNT; i++) {
            UUID id = UUID.randomUUID();
            MessageEntity entity = new MessageEntity();
            entity.setId(id);
            entity.setPhone(String.format("8(923)532%04d", random.nextInt(9999)));
            entity.setMessage(messages[i % messages.length]);
            entity.setCreateTimestamp(LocalDateTime.now());

            if (i % identifiersCount == 0) {
                identifiers.add(id);
            }

            service.save(entity);
        }
        log.info("Create records takes {} ms.", System.currentTimeMillis() - time);

        time = System.currentTimeMillis();
        List<MessageEntity> entities = service.getById(identifiers);
        log.info("Select {} records takes {} ms.", identifiers.size(), System.currentTimeMillis() - time);
        assertEquals(identifiers.size(), entities.size(), "Selected entities size and identifiers size differs.");
    }

    private void doBatchTest(MessageService service) {
        // Удаляем все записи, если таковые имеются.
        long time = System.currentTimeMillis();
        service.clear();
        log.info("Delete records takes {} ms.", System.currentTimeMillis() - time);

        time = System.currentTimeMillis();
        Set<UUID> identifiers = new HashSet<>();
        int identifiersCount = COUNT / 5;
        Random random = new SecureRandom();
        // добавляем N записей
        List<MessageEntity> entities = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            UUID id = UUID.randomUUID();
            MessageEntity entity = new MessageEntity();
            entity.setId(id);
            entity.setPhone(String.format("8(926)532%04d", random.nextInt(9999)));
            entity.setMessage(messages[i % messages.length]);
            entity.setCreateTimestamp(LocalDateTime.now());

            if (i % identifiersCount == 0) {
                identifiers.add(id);
            }

            entities.add(entity);
            if (entities.size() % BATCH_SIZE == 0) {
                service.saveBatch(entities);
                entities.clear();
            }
        }

        if (!entities.isEmpty()) {
            service.saveBatch(entities);
            entities.clear();
        }
        log.info("Create records takes {} ms.", System.currentTimeMillis() - time);

        time = System.currentTimeMillis();
        List<MessageEntity> selectedEntities = service.getById(identifiers);
        log.info("Select {} records takes {} ms.", identifiers.size(), System.currentTimeMillis() - time);
        assertEquals(identifiers.size(), selectedEntities.size(), "Selected entities size and identifiers size differs.");
    }
}