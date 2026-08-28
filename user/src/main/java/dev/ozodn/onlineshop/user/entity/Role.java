package dev.ozodn.onlineshop.user.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a user security role in the system.
 */
@Entity
@Table(name = "roles", schema = "user_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
}