package dev.ozodn.onlineshop.user.repository;

import dev.ozodn.onlineshop.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for {@link Role} persistence operations.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Retrieves a role entity by its unique name.
     *
     * @param name unique name of the role
     * @return an {@link Optional} containing the role if found, or empty otherwise
     */
    Optional<Role> findByName(String name);
}