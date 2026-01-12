package com.example.bookreviewapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;


import com.example.bookreviewapi.model.User;
import com.example.bookreviewapi.model.UserRole;
import com.example.bookreviewapi.repository.UserRepository;


@Component
@Profile("dev")
public class DevDataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger =  LoggerFactory.getLogger(DevDataInitializer.class);

    //Default admin user credentials
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_EMAIL = "admin@bookreview.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    @Override
    public void run(String... args) throws Exception {
        initializeAdminUser();
    }

    private void initializeAdminUser() {
        try {
            // Check if admin user already exists
            boolean adminExists = userRepository.findByRole(UserRole.ADMIN).isPresent();

            if (adminExists) {
                logger.info("Admin user already exists. Skipping user admin creation.");
                return;
            }

            // Create default Admin
            User admin = new User();
            admin.setUsername(DEFAULT_ADMIN_USERNAME);
            admin.setEmail(DEFAULT_ADMIN_EMAIL);
            admin.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
            admin.setRole(UserRole.ADMIN);

            User savedAdmin = userRepository.save(admin);

            logger.info("=== DEV ENVIRONMENT: DEFAULT ADMIN USER CREATED ===");
            logger.info("Username: {}", DEFAULT_ADMIN_USERNAME);
            logger.info("Password: {}", DEFAULT_ADMIN_PASSWORD);
            logger.info("Email: {}", DEFAULT_ADMIN_EMAIL);
            logger.info("User ID: {}", savedAdmin.getId());
            logger.info("=== IMPORTANT: Change these credentials immediately! ===");
            logger.info("=== Access H2 Console at: http://localhost:8080/h2-console ===");
        
        } catch (Exception e) {
            logger.info("Error initializing admin user: {}", e.getMessage(), e);
            // Don't throw an exception, let the applicatuion run and just log the error
        } 

    }
    
}
