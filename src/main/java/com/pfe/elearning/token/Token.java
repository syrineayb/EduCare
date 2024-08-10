    package com.pfe.elearning.token;

    import com.pfe.elearning.user.entity.User;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    public class Token {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        protected Integer id;


        @Column(length = 1000, unique = true)
        private String token;

        @Enumerated(EnumType.STRING)
        public TokenType tokenType = TokenType.BEARER;

        public boolean revoked;

        public boolean expired;

   //     @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
        @ManyToOne( fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
        @JoinColumn(name = "user_id")
        private User user;
    }