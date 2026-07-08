package com.example.PeakPerform.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.PeakPerform.entity.AppUserEntity;
import com.example.PeakPerform.enums.Role;
import com.example.PeakPerform.repository.AppUserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(AppUserRepository appUserRepository,
                      PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        seedPerformanceAdmin();
        seedTeamLead();
        seedGoalOwner();
    }

    private void seedPerformanceAdmin() {

        if (!appUserRepository.existsByEmail("admin@peakperform.com")) {

            AppUserEntity admin = new AppUserEntity();

            admin.setFullName("Performance Admin");
            admin.setEmail("admin@peakperform.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole(Role.ROLE_PERFORMANCE_ADMIN);

            // REQUIRED
            admin.setDepartment("Administration");

            admin.setIsActive(true);

            appUserRepository.save(admin);

            System.out.println("Performance Admin created.");
        }
    }

    private void seedTeamLead() {

        if (!appUserRepository.existsByEmail("lead@peakperform.com")) {

            AppUserEntity lead = new AppUserEntity();

            lead.setFullName("Team Lead");
            lead.setEmail("lead@peakperform.com");
            lead.setPassword(passwordEncoder.encode("Lead@123"));
            lead.setRole(Role.ROLE_TEAM_LEAD);

            // REQUIRED
            lead.setDepartment("Engineering");

            lead.setIsActive(true);

            appUserRepository.save(lead);

            System.out.println("Team Lead created.");
        }
    }

    private void seedGoalOwner() {

        if (!appUserRepository.existsByEmail("owner@peakperform.com")) {

            AppUserEntity owner = new AppUserEntity();

            owner.setFullName("Goal Owner");
            owner.setEmail("owner@peakperform.com");
            owner.setPassword(passwordEncoder.encode("Owner@123"));
            owner.setRole(Role.ROLE_GOAL_OWNER);

            // REQUIRED
            owner.setDepartment("Engineering");

            owner.setIsActive(true);

            appUserRepository.save(owner);

            System.out.println("Goal Owner created.");
        }
    }
}