package com.pfe.elearning.profile.repository;

import com.pfe.elearning.profile.entity.Profile;
import com.pfe.elearning.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile,Long> {
   // boolean existsByUserId(Long userId);
    Optional<Profile> findById(Integer userId);
    Profile findByEmail(String oldEmail);
    Optional<Profile> findByUser(User user);
    void deleteByUser(User user);
}
