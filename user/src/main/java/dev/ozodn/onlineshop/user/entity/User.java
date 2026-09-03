package dev.ozodn.onlineshop.user.entity;

import dev.ozodn.onlineshop.common.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a registered user account in the system.
 */
@Entity
@Table(name = "users", schema = "user_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends SoftDeletableEntity {

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            schema = "user_schema",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * Assigns the specified role to the user.
     *
     * @param role the {@link Role} to assign
     * @throws IllegalArgumentException if the role is {@code null}
     */
    public void addRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role must not be null");
        }
        roles.add(role);
    }
}