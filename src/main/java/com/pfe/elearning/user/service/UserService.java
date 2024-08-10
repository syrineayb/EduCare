package com.pfe.elearning.user.service;

import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.user.dto.ChangePasswordRequest;
import com.pfe.elearning.user.dto.UserRequest;
import com.pfe.elearning.user.dto.UserResponse;
import com.pfe.elearning.user.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.List;

public interface UserService {
    /* @Override
     public void create(UserRequest request,String adminEmail) {
         validator.validate(request);
         User user = new User();
         user.setFirstname(request.getFirstname());
         user.setLastname(request.getLastname());
         user.setEmail(request.getEmail());
         user.setPassword(passwordEncoder.encode(request.getPassword()));
         user.setActive(true);
        // user.setRoles(request.getRoles());

         userRepository.save(user);

         Profile profile = new Profile();
         profile.setUser(user);
         profile.setFirstName(request.getFirstname());
         profile.setLastName(request.getLastname());
         profile.setEmail(request.getEmail());
         profileRepository.save(profile);

         String emailSubject = "Account Created Successfully";
         String emailBody = "Dear " + request.getFirstname() + ",\n\nYour account has been successfully created.\n\nUsername: " +
                 request.getEmail() + "\nPassword: " + request.getPassword() + "\n\nCreated by: (" + adminEmail + ")\n\nThank you.";

         emailService.sendEmailToCreatedUser(request.getEmail(), emailSubject, emailBody);
     }



     */

 /* @Override
public void create(UserRequest request,String adminEmail) {
   validator.validate(request);
   User user = new User();
   user.setFirstname(request.getFirstname());
   user.setLastname(request.getLastname());
   user.setEmail(request.getEmail());
   user.setPassword(passwordEncoder.encode(request.getPassword()));
   user.setActive(true);
  // user.setRoles(request.getRoles());

   userRepository.save(user);

   Profile profile = new Profile();
   profile.setUser(user);
   profile.setFirstName(request.getFirstname());
   profile.setLastName(request.getLastname());
   profile.setEmail(request.getEmail());
   profileRepository.save(profile);

   String emailSubject = "Account Created Successfully";
   String emailBody = "Dear " + request.getFirstname() + ",\n\nYour account has been successfully created.\n\nUsername: " +
           request.getEmail() + "\nPassword: " + request.getPassword() + "\n\nCreated by: (" + adminEmail + ")\n\nThank you.";

   emailService.sendEmailToCreatedUser(request.getEmail(), emailSubject, emailBody);
}



*/
 List<User> findAllCandidatesOfPublisherCourses(Integer publisherId);

 // Method to create a new user
   // void create(UserRequest request, String adminEmail);
    void create(UserRequest request);
   // void create(UserRequest request, String adminEmail);
    int countTotalUsers();
    int countActiveUsers();
    int countStudents();
    int countInstructors();
    // Method to retrieve all users
    PageResponse<UserResponse> findAll(int page, int size);    // Method to find a user by ID
    UserResponse findById(Integer id);
    UserResponse update(Integer userId, UserRequest request);

    // Method to delete a user by ID
    void deleteUser(Integer userId);
    // Method to find all users by state (active or inactive) with pagination
   // PageResponse<UserResponse> findAllUsersByState(boolean active, int page, int size);

    // Method to change user password
    void changePassword(ChangePasswordRequest request, Principal connectedUser);

    // Method to get a user by email
    User getUserByEmail(String userEmail);


   // List<UserResponse> findAllUsers();

    List<UserResponse> getUsersByFirstname(String name);
    List<UserResponse> getUsersByLastname(String name);

    List<UserResponse> getUsersByRole(String role);
    List<UserResponse> searchByEmail(String email) ;

    void updateUserActiveStatus(Integer userId, boolean active);
    boolean getUserActiveStatus(Integer userId);
    void deactivateInactiveUsers(int daysInactive);
    List<User> findInactiveUsers(int daysInactive);
    void activateUser(Integer userId);

    boolean checkEmailExists(String email);

 // Méthode ajoutée pour vérifier l'existence de l'email puis envoyer un message au candidat avec un code aléatoire
    void deleteCurrentUserAccount();
    void deactivateCurrentUserAccount();

      UserResponse getCurrentLoggedInUser();


    //  added by syrine
   // UserDetails getCurrentLoggedInUser();

 // Method to initiate forgot password process
 void forgotPassword(String email, String adminEmail);
 boolean verifyCode(String email, String code);
 boolean checkEmailExistsAndSendCode(String email, String adminEmail);

 void changePassword(ChangePasswordRequest request, String email);
}
