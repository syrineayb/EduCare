package com.pfe.elearning.role;

        import com.pfe.elearning.user.entity.User;
        import com.fasterxml.jackson.annotation.JsonIgnore;
        import jakarta.persistence.*;

        import java.util.List;
        import lombok.AllArgsConstructor;
        import lombok.Builder;
        import lombok.Getter;
        import lombok.NoArgsConstructor;
        import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @ManyToMany(mappedBy = "roles", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<User> users;
    // Constructor with name parameter
    public Role(String name) {
        this.name = name;
    }

}