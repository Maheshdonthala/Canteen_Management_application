package com.jsp.canteen_management_system.config;

import com.jsp.canteen_management_system.model.Canteen;
import com.jsp.canteen_management_system.model.User;
import com.jsp.canteen_management_system.repository.CanteenRepository;
import com.jsp.canteen_management_system.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    CommandLineRunner init(CanteenRepository canteenRepository, UserRepository userRepository, BCryptPasswordEncoder encoder) {
        return args -> {
            try {
                // demo canteens
                if (canteenRepository.count() == 0) {
                    Canteen c1 = new Canteen();
                    c1.setName("Canteen 1");
                    c1.setLocation("Block A");
                    c1.setDefaultPlatePrice(5.0);
                    canteenRepository.save(c1);

                    Canteen c2 = new Canteen();
                    c2.setName("Canteen 2");
                    c2.setLocation("Block B");
                    c2.setDefaultPlatePrice(5.0);
                    canteenRepository.save(c2);
                }

                // demo user in DB (username: admin, password: guest)
                if (userRepository.findByUsername("admin").isEmpty()) {
                    User u = new User();
                    u.setUsername("admin");
                    u.setPassword(encoder.encode("guest"));
                    userRepository.save(u);
                }
            } catch (Exception ex) {
                // Don't let DB connectivity or other exceptions prevent the app from starting.
                logger.warn("DataLoader: skipping seed - could not access DB: {}", ex.getMessage());
            }
        };
    }
}
