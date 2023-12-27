package ru.gooamoko.springdataperformance.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.gooamoko.springdataperformance.entity.MessageEntity;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MessageServiceTest {
    private static final Logger log = LoggerFactory.getLogger(MessageServiceTest.class);
    private static final int COUNT = 500000;
    private String[] messages = new String[] {
      "Сегодня вам стоит избегать путешествий.",
      "Сегодня как никогда для вас будет важно внимание близкий людей.",
      "Сегодня воздержитесь от употребление алкоголя.",
      "Сегодня даже самое безобидное ваше высказывание по отношению к коллегам может перерасти в конфликт.",
      "Сегодня ваш счастливый день. Не бойтесь экспериментировать. Удача на вашей стороне.",
      "Сегодня вам нужно проявлять осторожность во всём. Особенно осторожно стоит отнестись к крупным покупкам.",
    };

    private final SpringDataMessageService springDataMessageService;
    private final EntityManagerMessageService entityManagerMessageService;

    @Autowired
    public MessageServiceTest(SpringDataMessageService springDataMessageService, EntityManagerMessageService entityManagerMessageService) {
        this.springDataMessageService = springDataMessageService;
        this.entityManagerMessageService = entityManagerMessageService;
    }


    @Test
    public void testSpringDataRepository() {
        log.info("Testing spring data repository.");
        long time = System.currentTimeMillis();
        dotest(springDataMessageService);
        log.info("Spring data takes {} ms.", System.currentTimeMillis() - time);
    }

    @Test
    public void testEntityManagerRepository() {
        log.info("Testing entity manager repository.");
        long time = System.currentTimeMillis();
        dotest(entityManagerMessageService);
        log.info("Entity manager takes {} ms.", System.currentTimeMillis() - time);
    }


    private void dotest(MessageService service) {
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
}