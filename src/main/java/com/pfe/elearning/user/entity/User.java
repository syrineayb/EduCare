    package com.pfe.elearning.user.entity;

    import com.pfe.elearning.course.entity.Course;
    import com.pfe.elearning.forum.Message.Message;
    import com.pfe.elearning.profile.entity.Profile;
    import com.pfe.elearning.genre.entity.Genre;
    import com.pfe.elearning.role.Role;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotBlank;
    import lombok.*;
    import org.hibernate.annotations.CreationTimestamp;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.Collection;
    import java.util.List;
    import java.util.stream.Collectors;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Entity
    @Table(name = "users")
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)

    public class User implements UserDetails  {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

      @Temporal(TemporalType.TIMESTAMP)
        @CreationTimestamp
        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt = LocalDateTime.now();
        @Column(name = "last_login")
        private LocalDateTime lastLogin;
       private String firstname;
        private String lastname;
        private String fullname ;
        @Column(nullable = false, unique = true)
        private String email;

        @NotBlank
        @Column(nullable = false)
        private String password;

        @Enumerated(EnumType.STRING)
        private Genre genre;
      private boolean enabled;
      private boolean active;

      // added
      // Add a field for verification code
      private String verificationCode;
     //   @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
        @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true)
        private Profile profile;
        @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
        private List<Role> roles;


        @ManyToMany(mappedBy = "enrolledCandidates", fetch = FetchType.EAGER)
        private List<Course> enrolledCourses;

/*
        @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL)
        private List<Forum> instructorForums = new ArrayList<>();


 */
         @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
         private List<Message> messages = new ArrayList<>();

     /*   @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Message> messages = new ArrayList<>();


      */
/*

        @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Answer> answers;


 */


        public String getFullname() {
            return this.firstname + " " + this.lastname;
        }
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return roles
                    .stream()
                    .map(r -> new SimpleGrantedAuthority(r.getName()))
                    .collect(Collectors.toList());
        }

        @Override
        public String getUsername() {
            return email;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
    }


    }