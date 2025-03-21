package org.example.expert;

import org.example.expert.config.JwtAuthenticationFilter;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserBulkRepository;
import org.example.expert.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
//@Import({JwtUtil.class, JwtAuthenticationFilter.class})
class ExpertApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(ExpertApplicationTests.class);
    @Autowired
    private UserBulkRepository userBulkRepository;

    @Test
    @Commit
    public void User_100만건_벌크_등록() {

        long startTime = System.currentTimeMillis();


        final int BATCH_SIZE = 1000;
        List<User> batchList = new ArrayList<>(BATCH_SIZE);
        for (int i = 0; i < 1000000; i++) {
            String email = "user" + i + "@test" + i + ".com";
            String nickname = RandomNicknameGenerator.generateNickname();
            User user = new User(email, "password", nickname, UserRole.ROLE_USER);
            batchList.add(user);

            if (batchList.size() == BATCH_SIZE) {
                userBulkRepository.saveAll(batchList);
                batchList.clear();

                sleep(500);
            }
        }

        if (!batchList.isEmpty()) {
            userBulkRepository.saveAll(batchList);
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Time: " + (endTime - startTime) + " ms");


    }

    private void sleep(int second) {
        try {
            Thread.sleep(second);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static class RandomNicknameGenerator {
        private static final String[] PRE_FIX = {"Happy", "Brave", "Clever", "Swift", "Lucky"};
        private static final String[] SUF_FIX = {"Tiger", "Penguin", "Koala", "Fox", "Panda"};
        private static final Random RANDOM = new Random();

        public static String generateNickname() {
            return PRE_FIX[RANDOM.nextInt(PRE_FIX.length)] +
                    SUF_FIX[RANDOM.nextInt(SUF_FIX.length)] +
                    RANDOM.nextInt(5);
        }

    }

}
