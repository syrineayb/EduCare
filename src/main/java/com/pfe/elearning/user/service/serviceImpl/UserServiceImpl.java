package com.pfe.elearning.user.service.serviceImpl;
import com.pfe.elearning.authentification.dto.request.RegisterRequest;
import com.pfe.elearning.authentification.dto.response.AuthResponse;
import com.pfe.elearning.authentification.service.AuthService;
import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.email.EmailService;
import com.pfe.elearning.profile.entity.Profile;
import com.pfe.elearning.profile.repository.ProfileRepository;
import com.pfe.elearning.role.Role;
import com.pfe.elearning.role.RoleRepository;
import com.pfe.elearning.role.RoleType;
import com.pfe.elearning.token.repository.TokenRepository;
import com.pfe.elearning.user.dto.ChangePasswordRequest;
import com.pfe.elearning.user.dto.UserMapper;
import com.pfe.elearning.user.dto.UserRequest;
import com.pfe.elearning.user.dto.UserResponse;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.repository.UserRepository;
import com.pfe.elearning.user.service.UserService;
import com.pfe.elearning.validator.ObjectsValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ObjectsValidator<UserRequest> validator;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final AuthService authService; // Injecting AuthService
    // Constants for password generation
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String OTHER_CHAR = "!@#$%&*()_+-=[]?";

    private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;
    private static final SecureRandom random = new SecureRandom();
    private final Set<RoleType> validRoles = EnumSet.of(RoleType.ROLE_USER, RoleType.ROLE_ADMIN, RoleType.ROLE_CANDIDATE, RoleType.ROLE_INSTRUCTOR, RoleType.ROLE_NO_ROLE);

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
   public List<User> findAllCandidatesOfPublisherCourses(Integer publisherId) {
       return userRepository.findAllCandidatesOfPublisherCourses(publisherId);
   }

    @Override
    public void create(UserRequest request) {
        validator.validate(request);

        // Convert RoleType to String
        String roleName = request.getRole().toString();

        // Find the role by name in the repository
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    // If the role does not exist, create a new role
                    Role newRole = new Role(roleName);
                    roleRepository.save(newRole);
                    return newRole;
                });

        // Convert UserRequest to RegisterRequest
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstname(request.getFirstname());
        registerRequest.setLastname(request.getLastname());
        registerRequest.setEmail(request.getEmail());
        registerRequest.setPassword(request.getPassword());
        registerRequest.setRole(request.getRole().toString());

        // Call the register method from AuthService
        AuthResponse authResponse = authService.register(registerRequest);

        // Optionally handle the returned authentication response
        if (authResponse != null) {
            // Retrieve the logged-in admin's email address
            String loggedInAdminEmail = authService.getUserEmail();

            // Construct the email subject
            String emailSubject = "Welcome to Our Platform";

            // Construct the email body with HTML markup
            String roleDisplayName = "";
            String additionalText = "";

            // Customize email content based on the user's role
            switch (roleName) {
                case "ROLE_CANDIDATE":
                    roleDisplayName = "Candidate";
                    additionalText = "You have successfully registered as a candidate on our platform.";
                    break;
                case "ROLE_INSTRUCTOR":
                    roleDisplayName = "Instructor";
                    additionalText = "You have successfully registered as an instructor on our platform.";
                    break;
                case "ROLE_ADMIN":
                    roleDisplayName = "Manager";
                    additionalText = "You have successfully registered as a manager on our platform.";
                    break;
                default:
                    roleDisplayName = "User";
                    additionalText = "You have successfully registered on our platform.";
                    break;
            }

            String emailBody = "<html><body>" +
                    "<h2>Hello " + request.getFirstname() + ",</h2>" +
                    "<p>" + additionalText + "</p>" +
                    "<p><strong>Login Details:</strong></p>" +
                    "<ul>" +
                    "<li><strong>Email:</strong> " + request.getEmail() + "</li>" +
                    "<li><strong>Password:</strong> " + request.getPassword() + "</li>" +
                    "</ul>" +
                    "<p><strong>Role:</strong> " + roleDisplayName + "</p>" +
                    "<p>Please log in to our platform using the provided credentials.</p>" +
                    "<p>Best regards,<br/>The Platform Team</p>" +
                    "</body></html>";

            // Send email to the created user with the currently logged-in admin as the sender
            emailService.sendEmail(request.getEmail(), emailSubject, emailBody, loggedInAdminEmail);
        }
    }



    @Override
    public int countTotalUsers() {
        return userRepository.countTotalUsers(); // Assuming userRepository has a count() method
    }

    @Override
    public int countActiveUsers() {
        return userRepository.countActiveUsers(true); // Assuming userRepository has a countByActiveTrue() method
    }
    @Override
    public int countStudents() {
        return userRepository.countCandidateUsers();
    }

    @Override
    public int countInstructors() {
        return userRepository.countInstructorUsers();
    }
    @Override
    public UserResponse findById(Integer id) {
        return userRepository.findById(id)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new EntityNotFoundException("No user found with the ID: " + id));
    }

    @Override
    public UserResponse update(Integer userId, UserRequest request) {
        // Retrieve the user from the database
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // Update the user's information with the new values
        existingUser.setFirstname(request.getFirstname());
        existingUser.setLastname(request.getLastname());
        existingUser.setEmail(request.getEmail());
        existingUser.setPassword(request.getPassword());
        existingUser.setGenre(request.getGenre());
        // Concatenate firstname and lastname to update username
        existingUser.setFullname(request.getFirstname() + " " + request.getLastname());
        User updatedUser = userRepository.save(existingUser);

        // Update profile information
        Profile profile = profileRepository.findByUser(existingUser)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found for user: " + existingUser.getId()));
        profile.setFirstName(request.getFirstname());
        profile.setLastName(request.getLastname());
        profile.setEmail(request.getEmail());
        profileRepository.save(profile);

        // Convert the updated user to UserResponse DTO and return
        return userMapper.toUserResponse(updatedUser);
    }

    @Transactional
    @Override
    public void deleteUser(Integer userId) {
        // Retrieve the user from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // Delete associated tokens
        tokenRepository.deleteByUserId(userId);

        // Delete the user
        userRepository.deleteById(userId);

        // Delete associated profile
        profileRepository.deleteByUser(user);
    }


    @Override
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        if (connectedUser == null) {
            throw new IllegalArgumentException("Principal is null");
        }

        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Passwords do not match");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        userRepository.save(user);
    }


    public void changePassword(ChangePasswordRequest request, String email) {
        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        // Check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Passwords do not match");
        }

        // Update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // Save the new password
        userRepository.save(user);

        // Send password change notification email
        // Send password change notification email
        String subject = "Password Changed Successfully";
        String body = "Dear Member,<br/><br/>"
                + "Your password has been successfully changed.<br/><br/>"
                + "If you did not initiate this change, please contact us immediately.<br/><br/>"
                + "Best regards,<br/>"
                + "EduCare";

// Styled HTML format for the email body
        String styledBody = "<div style=\"font-family: Arial, sans-serif;\">\n"
                + "<p>Dear Member,</p>\n\n"
                + "<p>Your password has been successfully changed.</p>\n\n"
                + "<p>If you did not initiate this change, please contact us immediately.</p>\n\n"
                + "<p>Best regards,<br/>\n"
                + "EduCare</p>\n"
                + "</div>";
        String senderName = "EduCare";

        emailService.sendEmail(email, subject, styledBody, senderName);
    }
    @Override
    public User getUserByEmail(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }

    /* @Override
     public List<UserResponse> findAllUsers() {
         // Retrieve all users from the database
         List<User> users = userRepository.findAll();

         // Map User entities to UserResponse DTOs
         return users.stream()
                 .map(userMapper::toUserResponse)
                 .collect(Collectors.toList());
     }

     */
    @Override
    public PageResponse<UserResponse> findAll(int page, int size) {
        Page<User> pageResult = userRepository.findAll(PageRequest.of(page, size));
        List<UserResponse> userResponses = pageResult.getContent().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());

        return PageResponse.<UserResponse>builder()
                .content(userResponses)
                .totalPages(pageResult.getTotalPages())
                .totalElements(pageResult.getTotalElements())
                .build();
    }

    @Override
    public List<UserResponse> getUsersByFirstname(String name) {
        List<User> users = userRepository.findByFirstnameContainingIgnoreCase(name);
        return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUsersByLastname(String name) {
        List<User> users = userRepository.findByLastnameContainingIgnoreCase(name);
        return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUsersByRole(String role) {
        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> searchByEmail(String email) {
        List<User> users = userRepository.findByEmailContaining(email);
        return users.stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void updateUserActiveStatus(Integer userId, boolean active) {
        // Retrieve the user from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Update the active status
        user.setActive(active);

        // Save the updated user
        userRepository.save(user);
    }

    @Override
    public boolean getUserActiveStatus(Integer userId) {
        // Retrieve the user from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Return the active status
        return user.isActive();
    }

    @Override
    public void deactivateInactiveUsers(int daysInactive) {
        List<User> inactiveUsers = findInactiveUsers(daysInactive);
        for (User user : inactiveUsers) {
            user.setActive(false);
            userRepository.save(user);
        }
    }

    @Override
    public List<User> findInactiveUsers(int daysInactive) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(daysInactive);
        return userRepository.findByLastLoginBeforeAndActiveIsTrue(threshold);
    }

    @Override
    public void activateUser(Integer userId) {
        // Retrieve the user from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Activate the user
        user.setActive(true);

        // Save the updated user
        userRepository.save(user);
    }

    @Override
    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean checkEmailExistsAndSendCode(String email, String adminEmail) {
        if (checkEmailExists(email)) {
            String randomCode = generateRandomCode();

            // Find the user by email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

            // Save the generated code to the user's record
            user.setVerificationCode(randomCode);
            userRepository.save(user);

            String emailSubject = "Verification Code";
            String emailBody = "<html><body style=\"font-family: Arial, sans-serif;\">" +
                    "<h2 style=\"color: #007bff;\">Hello,</h2>" +
                    "<p>You have requested a verification code to reset your password. Please use the following code to proceed:</p>" +
                    "<p style=\"background-color: #f0f0f0; padding: 10px; font-size: 18px;\"><strong>Verification Code: </strong>" + randomCode + "</p>" +
                    "<p>If you did not request this code, please ignore this email and consider changing your password or contacting our support team immediately.</p>" +
                    "<p style=\"font-style: italic;\">This code is valid for a limited time only.</p>" +
                    "<p>Thank you for using our service.</p>" +
                    "<p>Best regards,<br/><strong>EduCare</strong><br/>Contact us at: <a href=\"mailto:" + adminEmail + "\">" + adminEmail + "</a></p>" +
                    "</body></html>";

            String senderName = "EduCare";


            emailService.sendEmailToCreatedUser(email, emailSubject, emailBody);
            return true;
        }
        return false;
    }


    private String generateRandomCode() {
        // Generate a random 6-digit code
        return String.format("%06d", new Random().nextInt(999999));
    }



    @Transactional
    @Override
    public void deleteCurrentUserAccount() {
        // Get the authentication object for the current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get the username of the current user
        String username = authentication.getName();
        System.out.println("email: " + username);

        // Find the user by their email
        Optional<User> optionalUser = userRepository.findByEmail(username);

        // Check if the user exists
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Delete associated tokens
            tokenRepository.deleteByUser(user);

            // Delete the user
            userRepository.delete(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public void deactivateCurrentUserAccount() {
        // Get the authentication object for the current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get the username of the current user
        String username = authentication.getName();
        System.out.println("email: " + username);

        // Find the user by their email
        Optional<User> optionalUser = userRepository.findByEmail(username);

        // Check if the user exists
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Check if user is already deactivated
            if (!user.isActive()) {
                System.out.println("User is already deactivated");
                return; // Exit method if user is already deactivated
            }

            // Set the active status of the user to false
            user.setActive(false);

            // Save the changes to the user entity in the database
            userRepository.save(user);

            System.out.println("User deactivated successfully.");
        } else {
            throw new RuntimeException("User not found");
        }
    }
    @Override
    public UserResponse getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Find the user by their email
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

            // Map the user entity to a UserResponse DTO
            return userMapper.toUserResponse(user);
        }
        return null;
    }


   /* @Override
    public UserDetails getCurrentLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }
        return null; // Or throw an exception if user is not authenticated
    }
    */
   @Override
   public void forgotPassword(String email, String adminEmail) {
       // Find the user by email
       User user = userRepository.findByEmail(email)
               .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

       // Notify the admin about the password reset request
       String adminSubject = "Password Reset Request for User";
       String adminBody = "Dear Admin,\n\nA password reset request has been made for the user with email: " + email + ".\n"
               + "Please resend credentials to this user.\n\nThank you.";

       // Send email to the admin
       emailService.sendEmailToAdmins("admin",adminEmail, adminSubject, adminBody);
   }

    @Override
    public boolean verifyCode(String email, String code) {
        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        // Validate the code
        if (code.equals(user.getVerificationCode())) {
            // Clear the verification code after successful verification
            user.setVerificationCode(null); // Optionally clear the verification code after successful verification
            userRepository.save(user); // Save the user entity
            return true;
        } else {
            return false;
        }
    }

 /*  @Override
   public void forgotPassword(String email, String adminEmail) {
       // Find the user by email
       User user = userRepository.findByEmail(email)
               .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

       // Notify admin about password reset request
       String adminSubject = "Password Reset Request for User";
       String adminBody = "Dear Admin,\n\nA password reset request has been made for the user with email: " + email + ".\n"
               + "Please resend credentials to this user.\n\nThank you.";

       // Send email to admin
       emailService.sendEmail(adminEmail, adminSubject, adminBody, email);
       // Generate new password
       String newPassword = generateNewPassword(); // Replace with your password generation logic

       // Update user's password in the database
       user.setPassword(passwordEncoder.encode(newPassword));
       userRepository.save(user);

       // Send new credentials to the user
       String userSubject = "Your New Credentials";
       String userBody = "Dear " + user.getFirstname() + ",\n\nYour new credentials are:\n\n"
               + "Username: " + email + "\nPassword: " + newPassword + "\n\nPlease login and change your password.\n\nThank you.";
       emailService.sendEmail(email, userSubject, userBody, adminEmail);
   }

  */
  /*  private String generateNewPassword() {
        int passwordLength = 10; // Adjust the length of the generated password here

        StringBuilder password = new StringBuilder(passwordLength);
        for (int i = 0; i < passwordLength; i++) {
            int randomIndex = random.nextInt(PASSWORD_ALLOW_BASE.length());
            password.append(PASSWORD_ALLOW_BASE.charAt(randomIndex));
        }
        return password.toString();
    }

   */
}