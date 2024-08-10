package com.pfe.elearning.user.controller;

import com.pfe.elearning.common.PageResponse;
import com.pfe.elearning.email.EmailService;
import com.pfe.elearning.user.dto.ChangePasswordRequest;
import com.pfe.elearning.user.dto.UserRequest;
import com.pfe.elearning.user.dto.UserResponse;
import com.pfe.elearning.user.dto.UserStatsResponse;
import com.pfe.elearning.user.entity.User;
import com.pfe.elearning.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users")
public class userController {
    private final UserService userService;
    private final EmailService emailService;
    @Operation(
            description = "Saves a user to the database",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully created"),
                    @ApiResponse(responseCode = "403", description = "Missing or invalid JWT token")
            }
    )
    @GetMapping("/{publisherId}/candidates")
    public ResponseEntity<List<User>> findAllCandidatesOfPublisherCourses(@PathVariable Integer publisherId) {
        List<User> candidates = userService.findAllCandidatesOfPublisherCourses(publisherId);
        return new ResponseEntity<>(candidates, HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<UserStatsResponse> getUserStats() {
        int totalUsers = userService.countTotalUsers();
        int activeUsers = userService.countActiveUsers();
        int students = userService.countStudents();
        int instructors = userService.countInstructors();

        UserStatsResponse stats = new UserStatsResponse(totalUsers, activeUsers,students,instructors);
        return ResponseEntity.ok(stats);
    }
   /* @PostMapping
    public ResponseEntity<?> save(@RequestBody UserRequest userRequest, Principal principal) {
        String adminEmail = principal.getName(); // Assuming the admin's email is stored as the username in the Principal object
        userService.create(userRequest, adminEmail);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    */
   @PostMapping
   public ResponseEntity<?> save(@RequestBody UserRequest userRequest, Principal principal) {
       String adminEmail = principal.getName(); // Assuming the admin's email is stored as the username in the Principal object
       userService.create(userRequest);
       System.out.println("Role: "+ userRequest.getRole());
       return ResponseEntity.status(HttpStatus.CREATED).build();
   }



   /*@GetMapping
    public ResponseEntity<PageResponse<UserResponse>> findAll(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "20", required = false) int size
    ) {
        return ResponseEntity.ok(userService.findAll(page, size));
    }

    */

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<UserResponse> usersPage = userService.findAll(page, size);
        return ResponseEntity.ok(usersPage);
    }
    @GetMapping("/{user-id}")
    public ResponseEntity<UserResponse> findById(
            @PathVariable("user-id") Integer id
    ) {
        return ResponseEntity.ok(userService.findById(id));
    }
    @PatchMapping("/changePassword")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        try {
            userService.changePassword(request, connectedUser);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IllegalStateException e) {
            if (e.getMessage().equals("Wrong password")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Wrong password");
            } else if (e.getMessage().equals("Passwords do not match")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Passwords do not match");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while changing the password.");
            }
        }
    }


    @PutMapping("/{email}/changePassword")
    public ResponseEntity<?> changePassword(@PathVariable String email, @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request, email);
        // Instead of returning plain text, return a JSON object with a success message
        return ResponseEntity.ok().body("{\"message\": \"Password changed successfully.\"}");
    }
    @PutMapping("/{user-id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable("user-id") Integer userId,
            @RequestBody UserRequest userRequest
    ) {
        UserResponse updatedUser = userService.update(userId, userRequest);
        return ResponseEntity.ok(updatedUser);
    }
    //delete user by id
   @DeleteMapping("/{user-id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable("user-id") Integer userId
    ) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }



    @DeleteMapping("/deleteAccount")
    public ResponseEntity<String> deleteAccount() {
        // Logic to delete the current user's account
        userService.deleteCurrentUserAccount();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean emailExists = userService.checkEmailExists(email);
        return ResponseEntity.ok(emailExists);
    }
  /*  @PutMapping("/{userId}/active")
    public ResponseEntity<Void> updateUserActiveStatus(@PathVariable Integer userId, @RequestBody boolean active) {
        try {
            userService.updateUserActiveStatus(userId, active);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

   */
    @GetMapping("/by-firstname/{firstname}")
    public ResponseEntity<List<UserResponse>> getUsersByFirstname(@PathVariable String firstname) {
        List<UserResponse> users = userService.getUsersByFirstname(firstname);
        return ResponseEntity.ok(users);
    }
    @GetMapping("/by-lastname/{lastname}")
    public ResponseEntity<List<UserResponse>> getUsersByLastname(@PathVariable String lastname) {
        List<UserResponse> users = userService.getUsersByLastname(lastname);
        return ResponseEntity.ok(users);
    }
    @GetMapping("/by-role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRoleName(@PathVariable String role) {
        List<UserResponse> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }
    @GetMapping("/by-mail/{email}")
    public ResponseEntity<List<UserResponse>> searchUsersByEmail(@PathVariable String email) {
        List<UserResponse> users = userService.searchByEmail(email);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Integer userId) {
        userService.activateUser(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Integer userId) {
        userService.updateUserActiveStatus(userId, false);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{userId}/active")
    public ResponseEntity<Boolean> getUserActiveStatus(@PathVariable Integer userId) {
        boolean isActive = userService.getUserActiveStatus(userId);
        return ResponseEntity.ok(isActive);
    }

    @PutMapping("/deactivate-inactive")
    public ResponseEntity<Void> deactivateInactiveUsers(@RequestParam int daysInactive) {
        userService.deactivateInactiveUsers(daysInactive);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/deactivateCurrent")
    public ResponseEntity<Void> deactivateCurrentUserAccount() {
        try {
            // Deactivate the current user's account
            userService.deactivateCurrentUserAccount();
            // Return a response with status code 200 OK
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            // Handle the case where deactivation fails
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/current-user")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse currentUser = userService.getCurrentLoggedInUser();
        if (currentUser != null) {
            return ResponseEntity.ok(currentUser);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        try {
            userService.forgotPassword(email,"syrineayeb1@gmail.com");
            return ResponseEntity.ok("Password reset request sent successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process password reset request.");
        }
    }

  /*  @PostMapping("/check-email-and-send-code")
    public ResponseEntity<String> checkEmailExistsAndSendCode(@RequestParam String email) {
        boolean emailExists = userService.checkEmailExistsAndSendCode(email, "syrineayeb1@mail.com");
        if (emailExists) {
            return ResponseEntity.ok("Verification code sent successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found.");
        }
    }

   */
    @PostMapping("/check-email-and-send-code")
    public boolean checkEmailExistsAndSendCode(@RequestParam String email) {
        return userService.checkEmailExistsAndSendCode(email, "syrineayeb1@mail.com");
    }
    // Endpoint to verify the code
    @PostMapping("/verify-code")
    public boolean verifyCode(@RequestParam String email, @RequestParam String code) {
        // Call the service method to verify the code
        boolean codeVerified = userService.verifyCode(email, code);

        return codeVerified;
    }
}
