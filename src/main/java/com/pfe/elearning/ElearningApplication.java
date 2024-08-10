package com.pfe.elearning;

import com.pfe.elearning.config.FileStorageProperties;
import com.pfe.elearning.genre.entity.Genre;
import com.pfe.elearning.profile.entity.Profile;
import com.pfe.elearning.profile.repository.ProfileRepository;
import com.pfe.elearning.role.Role;
import com.pfe.elearning.role.RoleRepository;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
public class ElearningApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElearningApplication.class, args);
	}

	@Component
	public static class AdminUserInitializer implements ApplicationRunner {

		private final UserRepository userRepository;
		private final RoleRepository roleRepository;
		private final PasswordEncoder passwordEncoder;
		private final ProfileRepository profileRepository;

		@Autowired
		public AdminUserInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,ProfileRepository profileRepository) {
			this.userRepository = userRepository;
			this.roleRepository = roleRepository;
			this.passwordEncoder = passwordEncoder;
			this.profileRepository = profileRepository;
		}

		@Override
		@Transactional
		public void run(ApplicationArguments args) {
			// Check if the admin user already exists
			if (userRepository.existsByEmail("syrineayeb1@gmail.com")) {
				return;
			}

			Role adminRole = roleRepository.findByName("ROLE_ADMIN")
					.orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

			User adminUser = User.builder()
					.firstname("Admin")
					.lastname("Admin")
					.email("syrineayeb1@gmail.com")
					.password(passwordEncoder.encode("admin1234"))
					.enabled(true)
					.active(true)
					.fullname("Administrator")
					.roles(List.of(adminRole))
					.build();

			userRepository.save(adminUser);
			Profile adminProfile = new Profile();
			adminProfile.setUser(adminUser);
			adminProfile.setFirstName("Admin");
			adminProfile.setLastName("Admin");
			adminProfile.setEmail("syrineayeb1@gmail.com");
			adminProfile.setDescription("Administrator profile"); // Example: Setting a description
			adminProfile.setPhoneNumber("1234567890"); // Example: Setting a phone number
			adminProfile.setCountry("Country"); // Example: Setting a country
			adminProfile.setCurrentJob("Administrator"); // Example: Setting a current job
			adminProfile.setExperience(5); // Example: Setting experience
			adminProfile.setLinkedInUrl("admin"); // Example: Setting a LinkedIn URL
			adminProfile.setGithubUrl("admin"); // Example: Setting a GitHub URL
			adminProfile.setTwitterUrl("admin"); // Example: Setting a Twitter URL
			adminProfile.setGender(Genre.male); // Example: Setting gender
			adminProfile.setCountry("Tunisia");
			// Set other profile attributes as needed
			profileRepository.save(adminProfile);
		}
	}
}